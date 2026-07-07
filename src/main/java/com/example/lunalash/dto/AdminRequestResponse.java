package com.example.lunalash.dto;

import com.example.lunalash.entity.AdminAccountRequestEntity;

import java.time.LocalDateTime;

public class AdminRequestResponse {

    private Long requestId;
    private String requestType;
    private String status;
    private String requestedByUsername;
    private String targetUsername;
    private String targetName;
    private String newUsername;
    private String newName;
    private String reviewedByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;

    public AdminRequestResponse(AdminAccountRequestEntity request) {
        this.requestId = request.getRequestId();
        this.requestType = request.getRequestType().name();
        this.status = request.getStatus().name();
        this.requestedByUsername = request.getRequestedByUsername();
        this.targetUsername = request.getTargetUsername();
        this.targetName = request.getTargetName();
        this.newUsername = request.getNewUsername();
        this.newName = request.getNewName();
        this.reviewedByUsername = request.getReviewedByUsername();
        this.createdAt = request.getCreatedAt();
        this.reviewedAt = request.getReviewedAt();
    }

    public Long getRequestId() { return requestId; }
    public String getRequestType() { return requestType; }
    public String getStatus() { return status; }
    public String getRequestedByUsername() { return requestedByUsername; }
    public String getTargetUsername() { return targetUsername; }
    public String getTargetName() { return targetName; }
    public String getNewUsername() { return newUsername; }
    public String getNewName() { return newName; }
    public String getReviewedByUsername() { return reviewedByUsername; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
}
