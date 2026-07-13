package com.example.lunalash.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class SlotSetRequest {

    @NotNull(message = "日期不能為空")
    private LocalDate date;

    // 這一天要開放的時段清單，不在清單裡的固定時段會被設為不開放；傳空陣列代表整天都不開放
    @NotNull(message = "開放時段清單不能是 null")
    private List<LocalTime> openTimes;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public List<LocalTime> getOpenTimes() { return openTimes; }
    public void setOpenTimes(List<LocalTime> openTimes) { this.openTimes = openTimes; }
}
