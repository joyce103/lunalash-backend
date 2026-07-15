package com.example.lunalash.repository;

import com.example.lunalash.entity.AppointmentSlotLockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentSlotLockRepository extends JpaRepository<AppointmentSlotLockEntity, Long> {

    // 訪客查詢時段狀態、送出預約前檢查衝突用
    List<AppointmentSlotLockEntity> findBySlotDate(LocalDate slotDate);

    boolean existsBySlotDateAndSlotTime(LocalDate slotDate, LocalTime slotTime);
}
