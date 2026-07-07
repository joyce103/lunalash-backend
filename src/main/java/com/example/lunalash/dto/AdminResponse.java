package com.example.lunalash.dto;

import com.example.lunalash.entity.AdminEntity;

public class AdminResponse {

    private Long adminId;
    private String username;
    private String name;
    private String role;

    public AdminResponse(AdminEntity admin) {
        this.adminId = admin.getAdminId();
        this.username = admin.getUsername();
        this.name = admin.getName();
        this.role = admin.getRole().name();
    }

    public Long getAdminId() { return adminId; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getRole() { return role; }
}
