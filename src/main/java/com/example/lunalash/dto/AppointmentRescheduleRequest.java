package com.example.lunalash.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

// 後台調整預約時間用
public class AppointmentRescheduleRequest {

    @NotNull(message = "請選擇新的預約日期")
    private LocalDate date;

    @NotNull(message = "請選擇新的預約時段")
    private LocalTime time;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }
}
