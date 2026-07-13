package com.example.lunalash.controller;

import com.example.lunalash.dto.AppointmentCreateRequest;
import com.example.lunalash.dto.AppointmentResponse;
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

    @Operation(summary = "訪客送出預約申請")
    @PostMapping
    public AppointmentResponse create(@Valid @RequestBody AppointmentCreateRequest request) {
        return appointmentService.createAppointment(request);
    }
}
