package com.example.lunalash.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

// 核准預約時，每個被占用的固定時段都會在這裡新增一筆紀錄，(slot_date, slot_time) 有唯一索引。
// 一筆預約現在可能因為操作項目時間加總而橫跨好幾個固定時段，所以不能再用「一個時段一個生成欄位」的作法，
// 改成每個占用的時段各自一筆鎖定紀錄：核准時對每個時段都嘗試新增一筆，只要其中一個時段撞到唯一索引就整批失敗 rollback，
// 天然避免 race condition，也不需要額外的資料庫鎖。
@Entity
@Table(name = "appointment_slot_lock")
public class AppointmentSlotLockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_slot_lock_id")
    private Long appointmentSlotLockId;

    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    @Column(name = "slot_time", nullable = false)
    private LocalTime slotTime;

    @Column(name = "appointment_id", nullable = false)
    private Long appointmentId;

    public Long getAppointmentSlotLockId() { return appointmentSlotLockId; }
    public void setAppointmentSlotLockId(Long appointmentSlotLockId) { this.appointmentSlotLockId = appointmentSlotLockId; }

    public LocalDate getSlotDate() { return slotDate; }
    public void setSlotDate(LocalDate slotDate) { this.slotDate = slotDate; }

    public LocalTime getSlotTime() { return slotTime; }
    public void setSlotTime(LocalTime slotTime) { this.slotTime = slotTime; }

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
}
