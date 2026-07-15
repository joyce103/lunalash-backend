package com.example.lunalash.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointmentCreateRequest {

    @NotEmpty(message = "請至少選擇一個操作項目")
    private List<Long> operationItemIds;

    @NotNull(message = "請選擇預約日期")
    private LocalDate date;

    @NotNull(message = "請選擇預約時段")
    private LocalTime time;

    @NotBlank(message = "姓名不能為空")
    @Size(min = 2, max = 50, message = "姓名長度需為 2~50 字")
    private String customerName;

    @NotBlank(message = "電話不能為空")
    @Size(max = 20, message = "電話長度不能超過 20 字")
    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "電話只能包含數字、+、-、空格、括號")
    private String customerPhone;

    public List<Long> getOperationItemIds() { return operationItemIds; }
    public void setOperationItemIds(List<Long> operationItemIds) { this.operationItemIds = operationItemIds; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
}
