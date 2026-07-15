package com.example.lunalash.dto;

import com.example.lunalash.entity.AppointmentEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AppointmentResponse {

    private Long appointmentId;
    private List<OperationCatalogItemResponse> operationItems;
    private Integer totalDurationMinutes;
    private String customerName;
    private String customerPhone;
    private String lineUserId;
    private LocalDate date;
    private LocalTime time;
    private String status;
    private LocalDateTime createdAt;

    public AppointmentResponse(AppointmentEntity entity) {
        this.appointmentId = entity.getAppointmentId();
        this.operationItems = entity.getOperationItems().stream().map(OperationCatalogItemResponse::new).toList();
        this.totalDurationMinutes = entity.getTotalDurationMinutes();
        this.customerName = entity.getCustomerName();
        this.customerPhone = entity.getCustomerPhone();
        this.lineUserId = entity.getLineUserId();
        this.date = entity.getAppointmentDate();
        this.time = entity.getAppointmentTime();
        this.status = entity.getStatus().name();
        this.createdAt = entity.getCreatedAt();
    }

    public Long getAppointmentId() { return appointmentId; }
    public List<OperationCatalogItemResponse> getOperationItems() { return operationItems; }
    public Integer getTotalDurationMinutes() { return totalDurationMinutes; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getLineUserId() { return lineUserId; }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
