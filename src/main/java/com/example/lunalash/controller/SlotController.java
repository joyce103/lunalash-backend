package com.example.lunalash.controller;

import com.example.lunalash.dto.SlotStatusResponse;
import com.example.lunalash.service.AvailableSlotService;
import com.example.lunalash.service.OperationCatalogItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

// 訪客查詢某天時段狀態用，完全公開，不需要登入
@RestController
@RequestMapping("/api/available-slots")
@Tag(name = "預約-時段查詢 (公開)")
public class SlotController {

    private final AvailableSlotService availableSlotService;
    private final OperationCatalogItemService operationCatalogItemService;

    public SlotController(AvailableSlotService availableSlotService, OperationCatalogItemService operationCatalogItemService) {
        this.availableSlotService = availableSlotService;
        this.operationCatalogItemService = operationCatalogItemService;
    }

    @Operation(summary = "查詢某一天各時段的狀態 (AVAILABLE / BOOKED / CLOSED)，會依所選操作項目的時間加總判斷是否能容納")
    @GetMapping
    public List<SlotStatusResponse> getSlots(
            @Parameter(description = "日期，格式 yyyy-MM-dd")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "選擇的操作項目 id 清單，用逗號分隔")
            @RequestParam List<Long> operationItemIds
    ) {
        int totalDurationMinutes = operationCatalogItemService.sumDurationMinutes(operationItemIds);
        return availableSlotService.getSlotStatusForDate(date, totalDurationMinutes);
    }
}
