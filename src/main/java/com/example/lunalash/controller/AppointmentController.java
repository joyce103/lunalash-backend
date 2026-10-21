package com.example.lunalash.controller;

import com.example.lunalash.dto.AppointmentCreateRequest;
import com.example.lunalash.dto.AppointmentLookupRequest;
import com.example.lunalash.dto.AppointmentPublicResponse;
import com.example.lunalash.service.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 訪客送出預約，完全公開，不需要登入、不需要建立會員帳號
@RestController
@RequestMapping("/api/appointments")
@Tag(name = "預約 (公開)")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Operation(summary = "訪客送出預約申請 (回傳精簡資訊，含可用於之後查詢的查詢碼)")
    @PostMapping
    public AppointmentPublicResponse create(@Valid @RequestBody AppointmentCreateRequest request) {
        return appointmentService.createAppointment(request);
    }

    @Operation(summary = "客人用電話+查詢碼查詢預約狀態 (用 POST 避免電話出現在網址/log 中)")
    @PostMapping("/lookup")
    public AppointmentPublicResponse lookup(@Valid @RequestBody AppointmentLookupRequest request) {
        return appointmentService.lookup(request);
    }
}
