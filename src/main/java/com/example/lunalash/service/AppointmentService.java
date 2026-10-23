package com.example.lunalash.service;

import com.example.lunalash.dto.AppointmentCreateRequest;
import com.example.lunalash.dto.AppointmentLookupRequest;
import com.example.lunalash.dto.AppointmentPublicResponse;
import com.example.lunalash.dto.AppointmentRescheduleRequest;
import com.example.lunalash.dto.AppointmentResponse;
import com.example.lunalash.dto.DashboardStatsResponse;
import com.example.lunalash.entity.AppointmentEntity;
import com.example.lunalash.entity.AppointmentSlotLockEntity;
import com.example.lunalash.entity.AppointmentStatus;
import com.example.lunalash.entity.MemberEntity;
import com.example.lunalash.entity.OperationCatalogItemEntity;
import com.example.lunalash.exception.ResourceNotFoundException;
import com.example.lunalash.exception.SlotConflictException;
import com.example.lunalash.repository.AppointmentRepository;
import com.example.lunalash.repository.AppointmentSlotLockRepository;
import com.example.lunalash.repository.MemberRepository;
import com.example.lunalash.repository.OperationCatalogItemRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    // 查詢碼字元集：數字+大寫字母，排除容易混淆的 0/O/1/I/L
    private static final String QUERY_CODE_CHARS = "23456789ABCDEFGHJKMNPQRSTUVWXYZ";
    private static final int QUERY_CODE_LENGTH = 8;
    private final SecureRandom secureRandom = new SecureRandom();

    private final AppointmentRepository appointmentRepo;
    private final OperationCatalogItemRepository operationCatalogRepo;
    private final AppointmentSlotLockRepository slotLockRepo;
    private final MemberRepository memberRepo;
    private final AvailableSlotService availableSlotService;
    private final NotificationService notificationService;

    public AppointmentService(AppointmentRepository appointmentRepo,
                               OperationCatalogItemRepository operationCatalogRepo,
                               AppointmentSlotLockRepository slotLockRepo,
                               MemberRepository memberRepo,
                               AvailableSlotService availableSlotService,
                               NotificationService notificationService) {
        this.appointmentRepo = appointmentRepo;
        this.operationCatalogRepo = operationCatalogRepo;
        this.slotLockRepo = slotLockRepo;
        this.memberRepo = memberRepo;
        this.availableSlotService = availableSlotService;
        this.notificationService = notificationService;
    }

    // 訪客送出預約：只建立 PENDING 狀態，不會佔用時段，等後台審核。
    // guestEmail 是訪客用 Google 登入時、從已驗證過的 JWT 讀出來的 email；
    // 未登入 (匿名) 建立預約時傳 null 即可，跟現有行為完全一樣
    public AppointmentPublicResponse createAppointment(AppointmentCreateRequest request, String guestEmail) {
        List<OperationCatalogItemEntity> operationItems = operationCatalogRepo.findAllById(request.getOperationItemIds());
        if (operationItems.size() != request.getOperationItemIds().size()) {
            throw new IllegalArgumentException("有操作項目不存在，請重新整理頁面後再試一次");
        }
        if (operationItems.stream().anyMatch(item -> !Boolean.TRUE.equals(item.getIsActive()))) {
            throw new IllegalArgumentException("所選的操作項目中，有項目目前無法預約");
        }

        int totalDurationMinutes = operationItems.stream().mapToInt(OperationCatalogItemEntity::getDurationMinutes).sum();
        List<LocalTime> occupiedSlots = validateSlotAndGetOccupiedTimes(request.getDate(), request.getTime(), totalDurationMinutes);
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
        appointment.setQueryCode(generateUniqueQueryCode());
        appointment.setCustomerEmail(guestEmail);

        // 用電話自動比對現有會員；比對不到就維持訪客身分 (member 保持 null)，後台可以再手動連結
        findMemberByPhone(request.getCustomerPhone()).ifPresent(appointment::setMember);

        AppointmentEntity saved = appointmentRepo.save(appointment);
        notificationService.sendBookingReceived(saved);
        return new AppointmentPublicResponse(saved);
    }

    // 共用的時段合法性檢查，建立預約、後台改期都要過：不能是過去的日期、必須是合法的固定時段、
    // 時間加總不能超過營業時間、時段必須有開放。回傳這次會占用的所有固定時段，給呼叫端比對 slot lock 用
    private List<LocalTime> validateSlotAndGetOccupiedTimes(LocalDate date, LocalTime time, int totalDurationMinutes) {
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("不能預約過去的日期");
        }
        if (!AvailableSlotService.FIXED_SLOT_TIMES.contains(time)) {
            throw new IllegalArgumentException("不是合法的預約時段");
        }
        List<LocalTime> occupiedSlots = AvailableSlotService.getOccupiedSlotTimes(time, totalDurationMinutes);
        if (occupiedSlots == null) {
            throw new IllegalArgumentException("所選項目的時間加總超過營業時間，請重新選擇時段或減少項目");
        }
        if (!availableSlotService.areSlotsOpen(date, occupiedSlots)) {
            throw new IllegalArgumentException("此日期時段尚未開放預約");
        }
        return occupiedSlots;
    }

    // 查詢碼使用 SecureRandom 產生，萬一跟既有的碼重複 (機率極低) 就重新產生
    private String generateUniqueQueryCode() {
        String code;
        do {
            code = generateQueryCode();
        } while (appointmentRepo.existsByQueryCode(code));
        return code;
    }

    private String generateQueryCode() {
        StringBuilder sb = new StringBuilder(QUERY_CODE_LENGTH);
        for (int i = 0; i < QUERY_CODE_LENGTH; i++) {
            sb.append(QUERY_CODE_CHARS.charAt(secureRandom.nextInt(QUERY_CODE_CHARS.length())));
        }
        return sb.toString();
    }

    // 客人查詢預約狀態：電話只比對數字部分，查詢碼不分大小寫；
    // 電話錯誤跟查詢碼錯誤要回傳完全相同的錯誤訊息，避免讓人藉此判斷哪個欄位是對的
    public AppointmentPublicResponse lookup(AppointmentLookupRequest request) {
        String digitsOnly = request.getPhone().replaceAll("\\D", "");
        String normalizedCode = request.getQueryCode().trim().toUpperCase();

        AppointmentEntity appointment = appointmentRepo.findByQueryCode(normalizedCode)
                .filter(a -> digitsOnly.equals(a.getCustomerPhone().replaceAll("\\D", "")))
                .orElseThrow(() -> new ResourceNotFoundException("查無預約，請確認電話與查詢碼"));

        return new AppointmentPublicResponse(appointment);
    }

    // 訪客用 Google 登入後查詢「我的預約」：email 是從已驗證過的 JWT 讀出來的，不是前端自己填的，
    // 所以不用像 lookup() 那樣額外比對第二個欄位防猜測
    public List<AppointmentPublicResponse> findByCustomerEmail(String customerEmail) {
        return appointmentRepo.findByCustomerEmailOrderByAppointmentDateDescAppointmentTimeDesc(customerEmail).stream()
                .map(AppointmentPublicResponse::new)
                .toList();
    }

    // 會員電話欄位存的是純數字 (member.phone 長度限制 10 碼)，這裡把預約電話拿掉符號後再比對，
    // 才不會因為訪客填 "0912-345-678" 這種帶符號的格式而比對不到已存在的 "0912345678" 會員
    private java.util.Optional<MemberEntity> findMemberByPhone(String rawPhone) {
        String digitsOnly = rawPhone.replaceAll("\\D", "");
        return memberRepo.findByPhone(digitsOnly);
    }

    // 後台手動連結/取消連結會員：memberId 傳 null 代表取消連結，改回訪客身分
    public AppointmentResponse linkMember(Long appointmentId, Long memberId) {
        AppointmentEntity appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("查無此預約"));

        if (memberId == null) {
            appointment.setMember(null);
        } else {
            MemberEntity member = memberRepo.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("查無此會員"));
            appointment.setMember(member);
        }

        return new AppointmentResponse(appointmentRepo.save(appointment));
    }

    // 後台搜尋：姓名/電話/日期/狀態皆為選填
    public List<AppointmentResponse> search(String name, String phone, LocalDate date, AppointmentStatus status) {
        return appointmentRepo.search(blankToNull(name), blankToNull(phone), date, status).stream()
                .map(AppointmentResponse::new)
                .toList();
    }

    // 後台首頁月曆用：整個月所有狀態的預約，讓前端依日期分組畫成月曆
    public List<AppointmentResponse> getMonthAppointments(int year, int month) {
        java.time.YearMonth yearMonth = java.time.YearMonth.of(year, month);
        return appointmentRepo.findByAppointmentDateBetweenOrderByAppointmentDateAscAppointmentTimeAsc(
                        yearMonth.atDay(1), yearMonth.atEndOfMonth())
                .stream()
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

    // 後台調整預約時間：PENDING 直接改期；APPROVED 要連動搬移 slot lock；REJECTED 不允許改期
    @Transactional
    public AppointmentResponse reschedule(Long appointmentId, AppointmentRescheduleRequest request) {
        AppointmentEntity appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("查無此預約"));

        if (appointment.getStatus() == AppointmentStatus.REJECTED) {
            throw new IllegalArgumentException("已拒絕的預約不能改期");
        }
        if (appointment.getAppointmentDate().equals(request.getDate()) && appointment.getAppointmentTime().equals(request.getTime())) {
            throw new IllegalArgumentException("新的時間與原本時間相同，不需要改期");
        }

        List<LocalTime> newOccupiedSlots = validateSlotAndGetOccupiedTimes(
                request.getDate(), request.getTime(), appointment.getTotalDurationMinutes());

        if (appointment.getStatus() == AppointmentStatus.PENDING) {
            if (newOccupiedSlots.stream().anyMatch(time -> slotLockRepo.existsBySlotDateAndSlotTime(request.getDate(), time))) {
                throw new IllegalArgumentException("此時段已被預約，請選擇其他時段");
            }
        } else {
            // APPROVED：先刪掉這筆預約自己目前占用的 slot lock 並 flush，再寫入新時段的 lock 並 flush。
            // 如果新時段撞到唯一索引，丟出例外讓整筆交易 rollback，舊時段的 lock 就會自動恢復 (因為 delete 也會被 rollback)
            slotLockRepo.deleteByAppointmentId(appointmentId);
            slotLockRepo.flush();

            List<AppointmentSlotLockEntity> newLocks = newOccupiedSlots.stream().map(time -> {
                AppointmentSlotLockEntity lock = new AppointmentSlotLockEntity();
                lock.setSlotDate(request.getDate());
                lock.setSlotTime(time);
                lock.setAppointmentId(appointmentId);
                return lock;
            }).toList();

            try {
                slotLockRepo.saveAll(newLocks);
                slotLockRepo.flush();
            } catch (DataIntegrityViolationException e) {
                throw new SlotConflictException("此時段已被其他預約占用");
            }
        }

        LocalDate oldDate = appointment.getAppointmentDate();
        LocalTime oldTime = appointment.getAppointmentTime();
        appointment.setAppointmentDate(request.getDate());
        appointment.setAppointmentTime(request.getTime());

        AppointmentEntity saved = appointmentRepo.save(appointment);
        notificationService.sendRescheduled(saved, oldDate, oldTime);
        return new AppointmentResponse(saved);
    }

    // 查詢這筆預約在指定日期可以改到的開始時間列表：用這筆預約自己的總時長計算，
    // 並把它自己目前占用的 slot lock 視為空檔 (否則已核准的預約無法在同一天內調整時間)
    public List<LocalTime> getRescheduleSlots(Long appointmentId, LocalDate date) {
        AppointmentEntity appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("查無此預約"));

        Set<LocalTime> lockedTimes = slotLockRepo.findBySlotDate(date).stream()
                .filter(lock -> !lock.getAppointmentId().equals(appointmentId))
                .map(AppointmentSlotLockEntity::getSlotTime)
                .collect(Collectors.toSet());

        return AvailableSlotService.FIXED_SLOT_TIMES.stream()
                .filter(startTime -> {
                    List<LocalTime> occupied = AvailableSlotService.getOccupiedSlotTimes(startTime, appointment.getTotalDurationMinutes());
                    return occupied != null
                            && availableSlotService.areSlotsOpen(date, occupied)
                            && occupied.stream().noneMatch(lockedTimes::contains);
                })
                .toList();
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
