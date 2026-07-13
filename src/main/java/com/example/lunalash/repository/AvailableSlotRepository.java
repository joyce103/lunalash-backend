package com.example.lunalash.repository;

import com.example.lunalash.entity.AvailableDateEntity;
import com.example.lunalash.entity.AvailableSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailableSlotRepository extends JpaRepository<AvailableSlotEntity, Long> {

    // 訪客查詢某天有開放的時段
    List<AvailableSlotEntity> findByAvailableDate_AppointmentDateAndIsOpenTrue(LocalDate appointmentDate);

    // 後台編輯某天的時段設定用
    List<AvailableSlotEntity> findByAvailableDate(AvailableDateEntity availableDate);

    Optional<AvailableSlotEntity> findByAvailableDateAndSlotTime(AvailableDateEntity availableDate, LocalTime slotTime);
}
