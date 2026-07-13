package com.example.lunalash.controller;

import com.example.lunalash.dto.AvailableDateRequest;
import com.example.lunalash.dto.CalendarDayResponse;
import com.example.lunalash.service.AvailableDateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 需要登入才能呼叫，管理者決定哪些日期開放預約
@RestController
@RequestMapping("/api/admin/calendar")
@Tag(name = "預約-日期管理 (後台)")
public class AdminCalendarController {

    private final AvailableDateService availableDateService;

    public AdminCalendarController(AvailableDateService availableDateService) {
        this.availableDateService = availableDateService;
    }

    @Operation(summary = "查詢所有設定過的日期")
    @GetMapping
    public List<CalendarDayResponse> getAllDates() {
        return availableDateService.getAllDates();
    }

    @Operation(summary = "新增/重新開放一個可預約日期")
    @PostMapping
    public CalendarDayResponse addDate(@Valid @RequestBody AvailableDateRequest request) {
        return availableDateService.addDate(request);
    }

    @Operation(summary = "開放此日期")
    @PatchMapping("/{availableDateId}/open")
    public CalendarDayResponse openDate(@PathVariable Long availableDateId) {
        return availableDateService.setOpen(availableDateId, true);
    }

    @Operation(summary = "關閉此日期")
    @PatchMapping("/{availableDateId}/close")
    public CalendarDayResponse closeDate(@PathVariable Long availableDateId) {
        return availableDateService.setOpen(availableDateId, false);
    }

    @Operation(summary = "刪除此日期設定")
    @DeleteMapping("/{availableDateId}")
    public void deleteDate(@PathVariable Long availableDateId) {
        availableDateService.deleteDate(availableDateId);
    }
}
