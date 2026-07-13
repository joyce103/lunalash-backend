package com.example.lunalash.controller;

import com.example.lunalash.dto.DashboardStatsResponse;
import com.example.lunalash.service.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 需要登入才能呼叫
@RestController
@RequestMapping("/api/admin/dashboard")
@Tag(name = "後台儀表板")
public class AdminDashboardController {

    private final AppointmentService appointmentService;

    public AdminDashboardController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Operation(summary = "查詢儀表板統計數字 (今日預約數/待審核/本月預約數/已核准數)")
    @GetMapping("/stats")
    public DashboardStatsResponse getStats() {
        return appointmentService.getDashboardStats();
    }
}
