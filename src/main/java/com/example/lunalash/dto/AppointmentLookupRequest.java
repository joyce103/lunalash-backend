package com.example.lunalash.dto;

import jakarta.validation.constraints.NotBlank;

// 客人用電話+查詢碼查詢預約狀態；特意用 POST 而不是 GET，避免電話號碼出現在網址和 access log 中
public class AppointmentLookupRequest {

    @NotBlank(message = "電話不能為空")
    private String phone;

    @NotBlank(message = "查詢碼不能為空")
    private String queryCode;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getQueryCode() { return queryCode; }
    public void setQueryCode(String queryCode) { this.queryCode = queryCode; }
}
