package com.example.lunalash.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_account_request")
public class AdminAccountRequestEntity {

    public enum RequestType { REGISTER, DELETE }
    public enum RequestStatus { PENDING, APPROVED, REJECTED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false, length = 20)
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "requested_by_admin_id", nullable = false)
    private Long requestedByAdminId;

    @Column(name = "requested_by_username", nullable = false, length = 50)
    private String requestedByUsername;

    // DELETE 專用：欲刪除的帳號
    @Column(name = "target_admin_id")
    private Long targetAdminId;

    @Column(name = "target_username", length = 50)
    private String targetUsername;

    @Column(name = "target_name", length = 50)
    private String targetName;

    // REGISTER 專用：欲新增的帳號（密碼在送出申請當下就先 BCrypt 加密好，核准時直接沿用，資料庫不會存明碼）
    @Column(name = "new_username", length = 50)
    private String newUsername;

    @Column(name = "new_password_hash", length = 255)
    private String newPasswordHash;

    @Column(name = "new_name", length = 50)
    private String newName;

    @Column(name = "reviewed_by_admin_id")
    private Long reviewedByAdminId;

    @Column(name = "reviewed_by_username", length = 50)
    private String reviewedByUsername;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    // ===== Getter / Setter =====

    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }

    public RequestType getRequestType() { return requestType; }
    public void setRequestType(RequestType requestType) { this.requestType = requestType; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public Long getRequestedByAdminId() { return requestedByAdminId; }
    public void setRequestedByAdminId(Long requestedByAdminId) { this.requestedByAdminId = requestedByAdminId; }

    public String getRequestedByUsername() { return requestedByUsername; }
    public void setRequestedByUsername(String requestedByUsername) { this.requestedByUsername = requestedByUsername; }

    public Long getTargetAdminId() { return targetAdminId; }
    public void setTargetAdminId(Long targetAdminId) { this.targetAdminId = targetAdminId; }

    public String getTargetUsername() { return targetUsername; }
    public void setTargetUsername(String targetUsername) { this.targetUsername = targetUsername; }

    public String getTargetName() { return targetName; }
    public void setTargetName(String targetName) { this.targetName = targetName; }

    public String getNewUsername() { return newUsername; }
    public void setNewUsername(String newUsername) { this.newUsername = newUsername; }

    public String getNewPasswordHash() { return newPasswordHash; }
    public void setNewPasswordHash(String newPasswordHash) { this.newPasswordHash = newPasswordHash; }

    public String getNewName() { return newName; }
    public void setNewName(String newName) { this.newName = newName; }

    public Long getReviewedByAdminId() { return reviewedByAdminId; }
    public void setReviewedByAdminId(Long reviewedByAdminId) { this.reviewedByAdminId = reviewedByAdminId; }

    public String getReviewedByUsername() { return reviewedByUsername; }
    public void setReviewedByUsername(String reviewedByUsername) { this.reviewedByUsername = reviewedByUsername; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}
