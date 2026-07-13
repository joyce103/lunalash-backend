package com.example.lunalash.service;

import com.example.lunalash.dto.AppointmentCreateRequest;
import com.example.lunalash.dto.AppointmentResponse;
import com.example.lunalash.dto.DashboardStatsResponse;
import com.example.lunalash.entity.AppointmentEntity;
import com.example.lunalash.entity.AppointmentStatus;
import com.example.lunalash.entity.ServiceItemEntity;
import com.example.lunalash.exception.ResourceNotFoundException;
import com.example.lunalash.exception.SlotConflictException;
import com.example.lunalash.repository.AppointmentRepository;
import com.example.lunalash.repository.ServiceItemRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final ServiceItemRepository serviceItemRepo;
    private final AvailableSlotService availableSlotService;
    private final NotificationService notificationService;

    public AppointmentService(AppointmentRepository appointmentRepo,
                               ServiceItemRepository serviceItemRepo,
                               AvailableSlotService availableSlotService,
                               NotificationService notificationService) {
        this.appointmentRepo = appointmentRepo;
        this.serviceItemRepo = serviceItemRepo;
        this.availableSlotService = availableSlotService;
        this.notificationService = notificationService;
    }

    // 訪客送出預約：只建立 PENDING 狀態，不會佔用時段，等後台審核
    public AppointmentResponse createAppointment(AppointmentCreateRequest request) {
        if (request.getDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("不能預約過去的日期");
        }
        ServiceItemEntity service = serviceItemRepo.findById(request.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("找不到此服務項目"));
        if (!Boolean.TRUE.equals(service.getIsActive())) {
            throw new IllegalArgumentException("此服務項目目前無法預約");
        }
        if (!AvailableSlotService.FIXED_SLOT_TIMES.contains(request.getTime())) {
            throw new IllegalArgumentException("不是合法的預約時段");
        }
        if (!availableSlotService.isSlotOpen(request.getDate(), request.getTime())) {
            throw new IllegalArgumentException("此日期時段尚未開放預約");
        }
        if (appointmentRepo.existsByAppointmentDateAndAppointmentTimeAndStatus(request.getDate(), request.getTime(), AppointmentStatus.APPROVED)) {
            throw new IllegalArgumentException("此時段已被預約，請選擇其他時段");
        }
        long phoneDigitCount = request.getCustomerPhone().chars().filter(Character::isDigit).count();
        if (phoneDigitCount < 8 || phoneDigitCount > 15) {
            throw new IllegalArgumentException("請輸入正確的電話號碼");
        }

        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setServiceItem(service);
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

    // 核准：立即占用時段。若已經有其他 Approved 搶先占用同一時段，資料庫的唯一索引會擋下來，這裡轉成友善訊息
    @Transactional
    public AppointmentResponse approve(Long appointmentId) {
        AppointmentEntity appointment = getPendingAppointment(appointmentId);
        appointment.setStatus(AppointmentStatus.APPROVED);
        try {
            AppointmentEntity saved = appointmentRepo.saveAndFlush(appointment);
            notificationService.sendApproved(saved);
            return new AppointmentResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new SlotConflictException("此時段已被其他預約占用");
        }
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
