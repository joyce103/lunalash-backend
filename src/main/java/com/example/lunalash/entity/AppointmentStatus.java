package com.example.lunalash.entity;

public enum AppointmentStatus {
    PENDING,   // 等待審核，視為空檔，不占用時段
    APPROVED,  // 已核准，正式占用該時段
    REJECTED   // 已拒絕，視為空檔，不占用時段
}
