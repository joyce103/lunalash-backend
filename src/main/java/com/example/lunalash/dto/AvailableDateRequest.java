package com.example.lunalash.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AvailableDateRequest {

    @NotNull(message = "日期不能為空")
    private LocalDate date;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
