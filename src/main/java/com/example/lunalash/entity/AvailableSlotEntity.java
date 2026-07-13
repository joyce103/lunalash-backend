package com.example.lunalash.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "available_slot")
public class AvailableSlotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "available_slot_id")
    private Long availableSlotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "available_date_id", nullable = false)
    private AvailableDateEntity availableDate;

    @Column(name = "slot_time", nullable = false)
    private LocalTime slotTime;

    @Column(name = "is_open", nullable = false)
    private Boolean isOpen = true;

    // ===== Getter / Setter =====

    public Long getAvailableSlotId() { return availableSlotId; }
    public void setAvailableSlotId(Long availableSlotId) { this.availableSlotId = availableSlotId; }

    public AvailableDateEntity getAvailableDate() { return availableDate; }
    public void setAvailableDate(AvailableDateEntity availableDate) { this.availableDate = availableDate; }

    public LocalTime getSlotTime() { return slotTime; }
    public void setSlotTime(LocalTime slotTime) { this.slotTime = slotTime; }

    public Boolean getIsOpen() { return isOpen; }
    public void setIsOpen(Boolean isOpen) { this.isOpen = isOpen; }
}
