package com.example.lunalash.service;

import com.example.lunalash.dto.SlotSetRequest;
import com.example.lunalash.dto.SlotStatusResponse;
import com.example.lunalash.entity.AppointmentSlotLockEntity;
import com.example.lunalash.entity.AvailableDateEntity;
import com.example.lunalash.entity.AvailableSlotEntity;
import com.example.lunalash.repository.AppointmentSlotLockRepository;
import com.example.lunalash.repository.AvailableDateRepository;
import com.example.lunalash.repository.AvailableSlotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AvailableSlotService {

    // 每天固定的預約時段：10:00 ~ 20:00，每小時一個
    public static final List<LocalTime> FIXED_SLOT_TIMES = List.of(
            LocalTime.of(10, 0), LocalTime.of(11, 0), LocalTime.of(12, 0), LocalTime.of(13, 0),
            LocalTime.of(14, 0), LocalTime.of(15, 0), LocalTime.of(16, 0), LocalTime.of(17, 0),
            LocalTime.of(18, 0), LocalTime.of(19, 0), LocalTime.of(20, 0)
    );

    private final AvailableDateRepository dateRepo;
    private final AvailableSlotRepository slotRepo;
    private final AppointmentSlotLockRepository slotLockRepo;

    public AvailableSlotService(AvailableDateRepository dateRepo, AvailableSlotRepository slotRepo, AppointmentSlotLockRepository slotLockRepo) {
        this.dateRepo = dateRepo;
        this.slotRepo = slotRepo;
        this.slotLockRepo = slotLockRepo;
    }

    // 操作項目時間加總後，換算要占用幾個固定時段 (無條件進位，例如 90 分鐘要占用 2 個時段)
    public static int occupiedSlotCount(int totalDurationMinutes) {
        return (int) Math.ceil(totalDurationMinutes / 60.0);
    }

    // 從 startTime 開始，這次預約會連續占用哪些固定時段；如果會超出每日的固定時段範圍 (超過打烊時間) 就回傳 null
    public static List<LocalTime> getOccupiedSlotTimes(LocalTime startTime, int totalDurationMinutes) {
        int slotCount = occupiedSlotCount(totalDurationMinutes);
        int startIndex = FIXED_SLOT_TIMES.indexOf(startTime);
        if (startIndex < 0 || startIndex + slotCount > FIXED_SLOT_TIMES.size()) {
            return null;
        }
        return FIXED_SLOT_TIMES.subList(startIndex, startIndex + slotCount);
    }

    // 訪客查詢某天時段狀態：AVAILABLE 可預約 / BOOKED 已預約 / CLOSED 未開放
    // 這裡的「未開放」也包含「時間加總後會超過營業時間，或占用範圍裡有任何一個時段沒開放」的情況
    // Pending、Rejected 都視為空檔，只有 Approved 會真正占用時段 (反映在 appointment_slot_lock 表裡)
    public List<SlotStatusResponse> getSlotStatusForDate(LocalDate date, int totalDurationMinutes) {
        boolean dateOpen = dateRepo.findByAppointmentDate(date).map(AvailableDateEntity::getIsOpen).orElse(false);

        Set<LocalTime> openTimes = dateOpen
                ? slotRepo.findByAvailableDate_AppointmentDateAndIsOpenTrue(date).stream()
                    .map(AvailableSlotEntity::getSlotTime).collect(Collectors.toSet())
                : Set.of();

        Set<LocalTime> lockedTimes = slotLockRepo.findBySlotDate(date).stream()
                .map(AppointmentSlotLockEntity::getSlotTime)
                .collect(Collectors.toSet());

        return FIXED_SLOT_TIMES.stream()
                .map(time -> new SlotStatusResponse(time, resolveStatus(time, totalDurationMinutes, openTimes, lockedTimes)))
                .toList();
    }

    private String resolveStatus(LocalTime startTime, int totalDurationMinutes, Set<LocalTime> openTimes, Set<LocalTime> lockedTimes) {
        List<LocalTime> span = getOccupiedSlotTimes(startTime, totalDurationMinutes);
        if (span == null || !span.stream().allMatch(openTimes::contains)) return "CLOSED";
        if (span.stream().anyMatch(lockedTimes::contains)) return "BOOKED";
        return "AVAILABLE";
    }

    // 後台編輯用：這一天固定時段裡，哪些目前是開放的 (勾選狀態)
    public List<SlotStatusResponse> getSlotConfigForDate(LocalDate date) {
        Set<LocalTime> openTimes = dateRepo.findByAppointmentDate(date)
                .map(availableDate -> slotRepo.findByAvailableDate(availableDate).stream()
                        .filter(AvailableSlotEntity::getIsOpen)
                        .map(AvailableSlotEntity::getSlotTime)
                        .collect(Collectors.toSet()))
                .orElse(Set.of());

        return FIXED_SLOT_TIMES.stream()
                .map(time -> new SlotStatusResponse(time, openTimes.contains(time)))
                .toList();
    }

    // 後台設定：直接指定這一天要開放的時段清單 (不在清單裡的固定時段一律關閉)，會順便把這天標記為開放日期
    public List<SlotStatusResponse> setOpenTimesForDate(SlotSetRequest request) {
        LocalDate date = request.getDate();
        Set<LocalTime> requestedTimes = Set.copyOf(request.getOpenTimes());

        AvailableDateEntity availableDate = dateRepo.findByAppointmentDate(date)
                .orElseGet(() -> {
                    AvailableDateEntity newDate = new AvailableDateEntity();
                    newDate.setAppointmentDate(date);
                    return newDate;
                });
        availableDate.setIsOpen(true);
        dateRepo.save(availableDate);

        for (LocalTime time : FIXED_SLOT_TIMES) {
            AvailableSlotEntity slot = slotRepo.findByAvailableDateAndSlotTime(availableDate, time)
                    .orElseGet(() -> {
                        AvailableSlotEntity newSlot = new AvailableSlotEntity();
                        newSlot.setAvailableDate(availableDate);
                        newSlot.setSlotTime(time);
                        return newSlot;
                    });
            slot.setIsOpen(requestedTimes.contains(time));
            slotRepo.save(slot);
        }

        return getSlotConfigForDate(date);
    }

    // 檢查指定的一段連續時段是否「每一個」都是開放狀態 (送出預約前用)
    public boolean areSlotsOpen(LocalDate date, List<LocalTime> times) {
        AvailableDateEntity availableDate = dateRepo.findByAppointmentDate(date).orElse(null);
        if (availableDate == null || !Boolean.TRUE.equals(availableDate.getIsOpen())) return false;

        for (LocalTime time : times) {
            boolean open = slotRepo.findByAvailableDateAndSlotTime(availableDate, time)
                    .map(AvailableSlotEntity::getIsOpen).orElse(false);
            if (!open) return false;
        }
        return true;
    }
}
