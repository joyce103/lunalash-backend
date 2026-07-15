package com.example.lunalash.service;

import com.example.lunalash.dto.AppointmentCreateRequest;
import com.example.lunalash.dto.AppointmentResponse;
import com.example.lunalash.dto.DashboardStatsResponse;
import com.example.lunalash.entity.AppointmentEntity;
import com.example.lunalash.entity.AppointmentSlotLockEntity;
import com.example.lunalash.entity.AppointmentStatus;
import com.example.lunalash.entity.OperationCatalogItemEntity;
import com.example.lunalash.exception.ResourceNotFoundException;
import com.example.lunalash.exception.SlotConflictException;
import com.example.lunalash.repository.AppointmentRepository;
import com.example.lunalash.repository.AppointmentSlotLockRepository;
import com.example.lunalash.repository.OperationCatalogItemRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final OperationCatalogItemRepository operationCatalogRepo;
    private final AppointmentSlotLockRepository slotLockRepo;
    private final AvailableSlotService availableSlotService;
    private final NotificationService notificationService;

    public AppointmentService(AppointmentRepository appointmentRepo,
                               OperationCatalogItemRepository operationCatalogRepo,
                               AppointmentSlotLockRepository slotLockRepo,
                               AvailableSlotService availableSlotService,
                               NotificationService notificationService) {
        this.appointmentRepo = appointmentRepo;
        this.operationCatalogRepo = operationCatalogRepo;
        this.slotLockRepo = slotLockRepo;
        this.availableSlotService = availableSlotService;
        this.notificationService = notificationService;
    }

    // 訪客送出預約：只建立 PENDING 狀態，不會佔用時段，等後台審核
    public AppointmentResponse createAppointment(AppointmentCreateRequest request) {
        if (request.getDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("不能預約過去的日期");
        }
        if (!AvailableSlotService.FIXED_SLOT_TIMES.contains(request.getTime())) {
            throw new IllegalArgumentException("不是合法的預約時段");
        }

        List<OperationCatalogItemEntity> operationItems = operationCatalogRepo.findAllById(request.getOperationItemIds());
        if (operationItems.size() != request.getOperationItemIds().size()) {
            throw new IllegalArgumentException("有操作項目不存在，請重新整理頁面後再試一次");
        }
        if (operationItems.stream().anyMatch(item -> !Boolean.TRUE.equals(item.getIsActive()))) {
            throw new IllegalArgumentException("所選的操作項目中，有項目目前無法預約");
        }

        int totalDurationMinutes = operationItems.stream().mapToInt(OperationCatalogItemEntity::getDurationMinutes).sum();
        List<LocalTime> occupiedSlots = AvailableSlotService.getOccupiedSlotTimes(request.getTime(), totalDurationMinutes);
        if (occupiedSlots == null) {
            throw new IllegalArgumentException("所選項目的時間加總超過營業時間，請重新選擇時段或減少項目");
        }
        if (!availableSlotService.areSlotsOpen(request.getDate(), occupiedSlots)) {
            throw new IllegalArgumentException("此日期時段尚未開放預約");
        }
        if (occupiedSlots.stream().anyMatch(time -> slotLockRepo.existsBySlotDateAndSlotTime(request.getDate(), time))) {
            throw new IllegalArgumentException("此時段已被預約，請選擇其他時段");
        }
        long phoneDigitCount = request.getCustomerPhone().chars().filter(Character::isDigit).count();
        if (phoneDigitCount < 8 || phoneDigitCount > 15) {
            throw new IllegalArgumentException("請輸入正確的電話號碼");
        }

        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setOperationItems(operationItems);
        appointment.setTotalDurationMinutes(totalDurationMinutes);
        appointment.setCustomerName(request.getCustomerName().trim());
        appointment.setCustomerPhone(request.getCustomerPhone().trim());
        appointment.setAppointmentDate(request.getDate());
        appointment.setAppointmentTime(request.getTime());
        appointment.setStatus(AppointmentStatus.PENDING);

        AppointmentEntity saved = appointmentRepo.save(appointment);
        notificationService.sendBookingReceived(saved);
        return new AppointmentResponse(saved);
    }

    // 後台搜尋：姓名/電話/日期/狀態皆為選填
    public List<AppointmentResponse> search(String name, String phone, LocalDate date, AppointmentStatus status) {
        return appointmentRepo.search(blankToNull(name), blankToNull(phone), date, status).stream()
                .map(AppointmentResponse::new)
                .toList();
    }

    // 核准：把這次預約占用的每個固定時段都寫進 appointment_slot_lock (每個時段有唯一索引)。
    // 如果同一個時段已經被別的預約搶先核准占用，flush 時會違反唯一索引丟出例外，交易整批 rollback，轉成友善訊息。
    @Transactional
    public AppointmentResponse approve(Long appointmentId) {
        AppointmentEntity appointment = getPendingAppointment(appointmentId);

        List<LocalTime> occupiedSlots = AvailableSlotService.getOccupiedSlotTimes(
                appointment.getAppointmentTime(), appointment.getTotalDurationMinutes());
        if (occupiedSlots == null) {
            throw new IllegalArgumentException("此預約的時段設定異常，無法核准");
        }

        List<AppointmentSlotLockEntity> locks = occupiedSlots.stream().map(time -> {
            AppointmentSlotLockEntity lock = new AppointmentSlotLockEntity();
            lock.setSlotDate(appointment.getAppointmentDate());
            lock.setSlotTime(time);
            lock.setAppointmentId(appointment.getAppointmentId());
            return lock;
        }).toList();

        try {
            slotLockRepo.saveAll(locks);
            slotLockRepo.flush();
        } catch (DataIntegrityViolationException e) {
            throw new SlotConflictException("此時段已被其他預約占用");
        }

        appointment.setStatus(AppointmentStatus.APPROVED);
        AppointmentEntity saved = appointmentRepo.save(appointment);
        notificationService.sendApproved(saved);
        return new AppointmentResponse(saved);
    }

    public AppointmentResponse reject(Long appointmentId) {
        AppointmentEntity appointment = getPendingAppointment(appointmentId);
        appointment.setStatus(AppointmentStatus.REJECTED);
        AppointmentEntity saved = appointmentRepo.save(appointment);
        notificationService.sendRejected(saved);
        return new AppointmentResponse(saved);
    }

    public DashboardStatsResponse getDashboardStats() {
        LocalDate today = LocalDate.now();
        long todayCount = appointmentRepo.countByAppointmentDate(today);
        long pendingCount = appointmentRepo.countByStatus(AppointmentStatus.PENDING);
        long monthCount = appointmentRepo.countByAppointmentDateBetween(today.withDayOfMonth(1), today.withDayOfMonth(today.lengthOfMonth()));
        long approvedCount = appointmentRepo.countByStatus(AppointmentStatus.APPROVED);
        return new DashboardStatsResponse(todayCount, pendingCount, monthCount, approvedCount);
    }

    // 給排程用：找出明天所有已核准的預約
    public List<AppointmentEntity> findTomorrowApprovedAppointments() {
        return appointmentRepo.findByAppointmentDateAndStatus(LocalDate.now().plusDays(1), AppointmentStatus.APPROVED);
    }

    private AppointmentEntity getPendingAppointment(Long appointmentId) {
        AppointmentEntity appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("查無此預約"));
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new IllegalArgumentException("此預約已經被審核過，無法重複處理");
        }
        return appointment;
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
