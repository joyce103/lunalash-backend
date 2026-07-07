package com.example.lunalash.controller;

import com.example.lunalash.dto.AdminActionResponse;
import com.example.lunalash.dto.AdminRequestResponse;
import com.example.lunalash.dto.AdminResponse;
import com.example.lunalash.dto.ChangePasswordRequest;
import com.example.lunalash.dto.RegisterRequest;
import com.example.lunalash.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 這裡所有的 API 都會被 SecurityConfig 的 anyRequest().authenticated() 擋住，
// 必須先登入（帶合法 JWT）才能呼叫，避免任何人都能自行建立/刪除後台管理員帳號
@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理員帳號")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "查詢所有管理員帳號")
    @GetMapping("/list")
    public List<AdminResponse> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    @Operation(summary = "新增管理員帳號（最高管理員直接生效，一般管理員需送審核）")
    @PostMapping("/register")
    public AdminActionResponse register(@Valid @RequestBody RegisterRequest request, Authentication authentication) {
        return adminService.register(request, authentication.getName());
    }

    @Operation(summary = "刪除管理員帳號（最高管理員直接生效，一般管理員需送審核）")
    @DeleteMapping("/{adminId}")
    public AdminActionResponse deleteAdmin(@PathVariable Long adminId, Authentication authentication) {
        return adminService.deleteAdmin(adminId, authentication.getName());
    }

    @Operation(summary = "修改自己的登入密碼")
    @PutMapping("/password")
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication authentication) {
        adminService.changePassword(authentication.getName(), request);
    }

    @Operation(summary = "查詢待審核的帳號申請（僅限最高管理員）")
    @GetMapping("/requests")
    public List<AdminRequestResponse> getPendingRequests(Authentication authentication) {
        return adminService.getPendingRequests(authentication.getName());
    }

    @Operation(summary = "核准帳號申請（僅限最高管理員）")
    @PostMapping("/requests/{requestId}/approve")
    public void approveRequest(@PathVariable Long requestId, Authentication authentication) {
        adminService.approveRequest(requestId, authentication.getName());
    }

    @Operation(summary = "駁回帳號申請（僅限最高管理員）")
    @PostMapping("/requests/{requestId}/reject")
    public void rejectRequest(@PathVariable Long requestId, Authentication authentication) {
        adminService.rejectRequest(requestId, authentication.getName());
    }
}
