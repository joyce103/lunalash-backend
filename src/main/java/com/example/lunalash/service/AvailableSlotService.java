package com.example.lunalash.service;

import com.example.lunalash.dto.SlotSetRequest;
import com.example.lunalash.dto.SlotStatusResponse;
import com.example.lunalash.entity.AppointmentStatus;
import com.example.lunalash.entity.AvailableDateEntity;
import com.example.lunalash.entity.AvailableSlotEntity;
import com.example.lunalash.repository.AppointmentRepository;
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
    private final AppointmentRepository appointmentRepo;

    public AvailableSlotService(AvailableDateRepository dateRepo, AvailableSlotRepository slotRepo, AppointmentRepository appointmentRepo) {
        this.dateRepo = dateRepo;
        this.slotRepo = slotRepo;
        this.appointmentRepo = appointmentRepo;
    }

    // 訪客查詢某天時段狀態：AVAILABLE 可預約 / BOOKED 已預約 / CLOSED 未開放
    // Pending、Rejected 都視為空檔，只有 Approved 會真正占用時段
    public List<SlotStatusResponse> getSlotStatusForDate(LocalDate date) {
        boolean dateOpen = dateRepo.findByAppointmentDate(date).map(AvailableDateEntity::getIsOpen).orElse(false);

        Set<LocalTime> openTimes = dateOpen
                ? slotRepo.findByAvailableDate_AppointmentDateAndIsOpenTrue(date).stream()
                    .map(AvailableSlotEntity::getSlotTime).collect(Collectors.toSet())
                : Set.of();

        Set<LocalTime> bookedTimes = appointmentRepo.findByAppointmentDateAndStatus(date, AppointmentStatus.APPROVED).stream()
                .map(com.example.lunalash.entity.AppointmentEntity::getAppointmentTime)
                .collect(Collectors.toSet());

        return FIXED_SLOT_TIMES.stream()
                .map(time -> new SlotStatusResponse(time, resolveStatus(time, openTimes, bookedTimes)))
                .toList();
    }

    private String resolveStatus(LocalTime time, Set<LocalTime> openTimes, Set<LocalTime> bookedTimes) {
        if (!openTimes.contains(time)) return "CLOSED";
        if (bookedTimes.contains(time)) return "BOOKED";
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

    boolean isSlotOpen(LocalDate date, LocalTime time) {
        return dateRepo.findByAppointmentDate(date)
                .map(availableDate -> slotRepo.findByAvailableDateAndSlotTime(availableDate, time)
                        .map(AvailableSlotEntity::getIsOpen).orElse(false))
                .orElse(false);
    }
}
