package com.example.lunalash.dto;

import com.example.lunalash.entity.AvailableDateEntity;

import java.time.LocalDate;

public class CalendarDayResponse {

    private Long availableDateId;
    private LocalDate date;
    private Boolean isOpen;

    public CalendarDayResponse(AvailableDateEntity entity) {
        this.availableDateId = entity.getAvailableDateId();
        this.date = entity.getAppointmentDate();
        this.isOpen = entity.getIsOpen();
    }

    public Long getAvailableDateId() { return availableDateId; }
    public LocalDate getDate() { return date; }
    public Boolean getIsOpen() { return isOpen; }
}
