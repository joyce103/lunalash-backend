package com.example.lunalash.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "available_date")
public class AvailableDateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "available_date_id")
    private Long availableDateId;

    @Column(name = "appointment_date", nullable = false, unique = true)
    private LocalDate appointmentDate;

    @Column(name = "is_open", nullable = false)
    private Boolean isOpen = true;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    // ===== Getter / Setter =====

    public Long getAvailableDateId() { return availableDateId; }
    public void setAvailableDateId(Long availableDateId) { this.availableDateId = availableDateId; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public Boolean getIsOpen() { return isOpen; }
    public void setIsOpen(Boolean isOpen) { this.isOpen = isOpen; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
