package com.example.lunalash.controller;

import com.example.lunalash.dto.CalendarDayResponse;
import com.example.lunalash.service.AvailableDateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 訪客月曆用，完全公開，不需要登入
@RestController
@RequestMapping("/api/calendar")
@Tag(name = "預約-月曆 (公開)")
public class CalendarController {

    private final AvailableDateService availableDateService;

    public CalendarController(AvailableDateService availableDateService) {
        this.availableDateService = availableDateService;
    }

    @Operation(summary = "查詢某個月份哪些日期開放/未開放")
    @GetMapping
    public List<CalendarDayResponse> getCalendar(
            @Parameter(description = "年份，例如 2026") @RequestParam int year,
            @Parameter(description = "月份，1~12") @RequestParam int month
    ) {
        return availableDateService.getCalendarForMonth(year, month);
    }
}
