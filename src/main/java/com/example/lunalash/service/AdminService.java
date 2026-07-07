package com.example.lunalash.service;

import com.example.lunalash.dto.AdminActionResponse;
import com.example.lunalash.dto.AdminRequestResponse;
import com.example.lunalash.dto.AdminResponse;
import com.example.lunalash.dto.ChangePasswordRequest;
import com.example.lunalash.dto.RegisterRequest;
import com.example.lunalash.entity.AdminAccountRequestEntity;
import com.example.lunalash.entity.AdminAccountRequestEntity.RequestStatus;
import com.example.lunalash.entity.AdminAccountRequestEntity.RequestType;
import com.example.lunalash.entity.AdminEntity;
import com.example.lunalash.exception.ResourceNotFoundException;
import com.example.lunalash.repository.AdminAccountRequestRepository;
import com.example.lunalash.repository.AdminRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {

    private final AdminRepository adminRepo;
    private final AdminAccountRequestRepository requestRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepo, AdminAccountRequestRepository requestRepo, PasswordEncoder passwordEncoder) {
        this.adminRepo = adminRepo;
        this.requestRepo = requestRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AdminResponse> getAllAdmins() {
        return adminRepo.findAll().stream().map(AdminResponse::new).toList();
    }

    // 新增管理員帳號：最高管理員直接建立；一般管理員只能送出申請，等最高管理員審核
    public AdminActionResponse register(RegisterRequest request, String currentUsername) {
        AdminEntity currentAdmin = getAdminByUsername(currentUsername);
        String username = request.getUsername().trim();

        if (adminRepo.existsByUsername(username)) {
            throw new IllegalArgumentException("此帳號已被使用，請換一個帳號");
        }
        if (requestRepo.existsByStatusAndRequestTypeAndNewUsername(RequestStatus.PENDING, RequestType.REGISTER, username)) {
            throw new IllegalArgumentException("已經有一筆待審核的申請要新增此帳號，請勿重複送出");
        }

        if (currentAdmin.isSuperAdmin()) {
            AdminEntity admin = new AdminEntity();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
            admin.setName(request.getName().trim());
            return AdminActionResponse.executed(new AdminResponse(adminRepo.save(admin)));
        }

        AdminAccountRequestEntity req = new AdminAccountRequestEntity();
        req.setRequestType(RequestType.REGISTER);
        req.setRequestedByAdminId(currentAdmin.getAdminId());
        req.setRequestedByUsername(currentAdmin.getUsername());
        req.setNewUsername(username);
        req.setNewPasswordHash(passwordEncoder.encode(request.getPassword()));
        req.setNewName(request.getName().trim());
        return AdminActionResponse.pending(requestRepo.save(req).getRequestId());
    }

    // 刪除管理員帳號：最高管理員直接刪除；一般管理員只能送出申請，等最高管理員審核
    public AdminActionResponse deleteAdmin(Long targetAdminId, String currentUsername) {
        AdminEntity currentAdmin = getAdminByUsername(currentUsername);
        AdminEntity target = adminRepo.findById(targetAdminId)
                .orElseThrow(() -> new ResourceNotFoundException("查無此管理員帳號"));

        validateDeletable(target, currentAdmin);

        if (requestRepo.existsByStatusAndRequestTypeAndTargetAdminId(RequestStatus.PENDING, RequestType.DELETE, targetAdminId)) {
            throw new IllegalArgumentException("已經有一筆待審核的申請要刪除此帳號，請勿重複送出");
        }

        if (currentAdmin.isSuperAdmin()) {
            adminRepo.delete(target);
            return AdminActionResponse.executed(null);
        }

        AdminAccountRequestEntity req = new AdminAccountRequestEntity();
        req.setRequestType(RequestType.DELETE);
        req.setRequestedByAdminId(currentAdmin.getAdminId());
        req.setRequestedByUsername(currentAdmin.getUsername());
        req.setTargetAdminId(target.getAdminId());
        req.setTargetUsername(target.getUsername());
        req.setTargetName(target.getName());
        return AdminActionResponse.pending(requestRepo.save(req).getRequestId());
    }

    public void changePassword(String currentUsername, ChangePasswordRequest request) {
        AdminEntity admin = getAdminByUsername(currentUsername);

        if (!passwordEncoder.matches(request.getOldPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("原密碼不正確");
        }

        admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        adminRepo.save(admin);
    }

    // ===== 最高管理員的審核功能 =====

    public List<AdminRequestResponse> getPendingRequests(String currentUsername) {
        requireSuperAdmin(currentUsername);
        return requestRepo.findByStatusOrderByCreatedAtDesc(RequestStatus.PENDING).stream()
                .map(AdminRequestResponse::new)
                .toList();
    }

    public void approveRequest(Long requestId, String currentUsername) {
        AdminEntity reviewer = requireSuperAdmin(currentUsername);
        AdminAccountRequestEntity req = getPendingRequestById(requestId);

        if (req.getRequestType() == RequestType.REGISTER) {
            if (adminRepo.existsByUsername(req.getNewUsername())) {
                throw new IllegalArgumentException("帳號「" + req.getNewUsername() + "」已經被使用，無法核准，請改為駁回");
            }
            AdminEntity admin = new AdminEntity();
            admin.setUsername(req.getNewUsername());
            admin.setPassword(req.getNewPasswordHash());
            admin.setName(req.getNewName());
            adminRepo.save(admin);
        } else {
            AdminEntity target = adminRepo.findById(req.getTargetAdminId())
                    .orElseThrow(() -> new IllegalArgumentException("欲刪除的帳號已經不存在，請改為駁回"));
            validateDeletable(target, reviewer);
            adminRepo.delete(target);
        }

        markReviewed(req, reviewer, RequestStatus.APPROVED);
    }

    public void rejectRequest(Long requestId, String currentUsername) {
        AdminEntity reviewer = requireSuperAdmin(currentUsername);
        AdminAccountRequestEntity req = getPendingRequestById(requestId);
        markReviewed(req, reviewer, RequestStatus.REJECTED);
    }

    // ===== 內部小工具 =====

    private AdminEntity getAdminByUsername(String username) {
        return adminRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("查無此管理員帳號"));
    }

    private AdminEntity requireSuperAdmin(String currentUsername) {
        AdminEntity admin = getAdminByUsername(currentUsername);
        if (!admin.isSuperAdmin()) {
            throw new AccessDeniedException("僅限最高管理員操作");
        }
        return admin;
    }

    private AdminAccountRequestEntity getPendingRequestById(Long requestId) {
        AdminAccountRequestEntity req = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("查無此申請"));
        if (req.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("此申請已經被處理過，無法重複審核");
        }
        return req;
    }

    private void validateDeletable(AdminEntity target, AdminEntity currentAdmin) {
        if (target.getUsername().equals(currentAdmin.getUsername())) {
            throw new IllegalArgumentException("不能刪除自己目前登入中的帳號，請請其他管理員協助刪除");
        }
        if (adminRepo.count() <= 1) {
            throw new IllegalArgumentException("系統至少需保留一個管理員帳號，無法刪除");
        }
    }

    private void markReviewed(AdminAccountRequestEntity req, AdminEntity reviewer, RequestStatus status) {
        req.setStatus(status);
        req.setReviewedByAdminId(reviewer.getAdminId());
        req.setReviewedByUsername(reviewer.getUsername());
        req.setReviewedAt(LocalDateTime.now());
        requestRepo.save(req);
    }
}
