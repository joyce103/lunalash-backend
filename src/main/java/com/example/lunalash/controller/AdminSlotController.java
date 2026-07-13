package com.example.lunalash.controller;

import com.example.lunalash.dto.SlotSetRequest;
import com.example.lunalash.dto.SlotStatusResponse;
import com.example.lunalash.service.AvailableSlotService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// 需要登入才能呼叫，管理者設定每一天哪些固定時段開放預約
@RestController
@RequestMapping("/api/admin/slots")
@Tag(name = "預約-時段管理 (後台)")
public class AdminSlotController {

    private final AvailableSlotService availableSlotService;

    public AdminSlotController(AvailableSlotService availableSlotService) {
        this.availableSlotService = availableSlotService;
    }

    @Operation(summary = "查詢某一天固定時段的開放設定 (供編輯用勾選框)")
    @GetMapping
    public List<SlotStatusResponse> getSlotConfig(
            @Parameter(description = "日期，格式 yyyy-MM-dd")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return availableSlotService.getSlotConfigForDate(date);
    }

    @Operation(summary = "設定某一天要開放哪些固定時段")
    @PutMapping
    public List<SlotStatusResponse> setSlots(@Valid @RequestBody SlotSetRequest request) {
        return availableSlotService.setOpenTimesForDate(request);
    }
}
