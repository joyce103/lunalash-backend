package com.example.lunalash.dto;

import com.example.lunalash.entity.AppointmentEntity;
import com.example.lunalash.entity.OperationCatalogItemEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// 客人建立預約的回應、以電話+查詢碼查詢預約狀態的回應，都用這個精簡版 DTO，
// 不可包含 customerPhone、lineUserId 等內部/隱私欄位
public class AppointmentPublicResponse {

    private String queryCode;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<String> operationItemNames;
    private Integer totalDurationMinutes;
    private String status;

    public AppointmentPublicResponse(AppointmentEntity entity) {
        this.queryCode = entity.getQueryCode();
        this.date = entity.getAppointmentDate();
        this.startTime = entity.getAppointmentTime();
        this.endTime = entity.getAppointmentTime().plusMinutes(entity.getTotalDurationMinutes());
        this.operationItemNames = entity.getOperationItems().stream().map(OperationCatalogItemEntity::getName).toList();
        this.totalDurationMinutes = entity.getTotalDurationMinutes();
        this.status = entity.getStatus().name();
    }

    public String getQueryCode() { return queryCode; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public List<String> getOperationItemNames() { return operationItemNames; }
    public Integer getTotalDurationMinutes() { return totalDurationMinutes; }
    public String getStatus() { return status; }
}
