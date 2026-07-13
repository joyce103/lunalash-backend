package com.example.lunalash.controller;

import com.example.lunalash.dto.ServiceItemResponse;
import com.example.lunalash.service.ServiceItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 訪客預約表單用，完全公開，不需要登入
@RestController
@RequestMapping("/api/services")
@Tag(name = "預約-服務項目 (公開)")
public class ServiceController {

    private final ServiceItemService serviceItemService;

    public ServiceController(ServiceItemService serviceItemService) {
        this.serviceItemService = serviceItemService;
    }

    @Operation(summary = "查詢所有啟用中的服務項目")
    @GetMapping
    public List<ServiceItemResponse> getActiveServices() {
        return serviceItemService.getActiveServices();
    }
}
