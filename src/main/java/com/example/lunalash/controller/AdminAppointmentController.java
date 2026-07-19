package com.example.lunalash.controller;

import com.example.lunalash.dto.AppointmentResponse;
import com.example.lunalash.entity.AppointmentStatus;
import com.example.lunalash.service.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// 需要登入才能呼叫，管理者搜尋/審核預約
@RestController
@RequestMapping("/api/admin/appointments")
@Tag(name = "預約管理 (後台)")
public class AdminAppointmentController {

    private final AppointmentService appointmentService;

    public AdminAppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Operation(summary = "搜尋預約 (姓名/電話/日期/狀態皆為選填)")
    @GetMapping
    public List<AppointmentResponse> search(
            @Parameter(description = "姓名關鍵字") @RequestParam(required = false) String name,
            @Parameter(description = "電話關鍵字") @RequestParam(required = false) String phone,
            @Parameter(description = "日期，格式 yyyy-MM-dd") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "狀態：PENDING / APPROVED / REJECTED") @RequestParam(required = false) AppointmentStatus status
    ) {
        return appointmentService.search(name, phone, date, status);
    }

    @Operation(summary = "查詢某個月份的所有預約 (供後台首頁月曆使用，含所有狀態)")
    @GetMapping("/calendar")
    public List<AppointmentResponse> getMonthAppointments(
            @Parameter(description = "年份，例如 2026") @RequestParam int year,
            @Parameter(description = "月份，1~12") @RequestParam int month
    ) {
        return appointmentService.getMonthAppointments(year, month);
    }

    @Operation(summary = "核准預約 (立即占用時段，若已被搶先占用會回傳 409)")
    @PatchMapping("/{appointmentId}/approve")
    public AppointmentResponse approve(@PathVariable Long appointmentId) {
        return appointmentService.approve(appointmentId);
    }

    @Operation(summary = "拒絕預約")
    @PatchMapping("/{appointmentId}/reject")
    public AppointmentResponse reject(@PathVariable Long appointmentId) {
        return appointmentService.reject(appointmentId);
    }
}
