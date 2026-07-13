package com.example.lunalash.dto;

import java.time.LocalTime;

public class SlotStatusResponse {

    // 給訪客看的三種狀態：AVAILABLE 可預約 / BOOKED 已預約 / CLOSED 未開放
    // 給後台編輯用時只會用到 isOpen（是否勾選開放）
    private LocalTime time;
    private String status;
    private Boolean isOpen;

    public SlotStatusResponse(LocalTime time, String status) {
        this.time = time;
        this.status = status;
    }

    public SlotStatusResponse(LocalTime time, Boolean isOpen) {
        this.time = time;
        this.isOpen = isOpen;
    }

    public LocalTime getTime() { return time; }
    public String getStatus() { return status; }
    public Boolean getIsOpen() { return isOpen; }
}
