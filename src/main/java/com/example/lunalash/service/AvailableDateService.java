package com.example.lunalash.service;

import com.example.lunalash.dto.AvailableDateRequest;
import com.example.lunalash.dto.CalendarDayResponse;
import com.example.lunalash.entity.AvailableDateEntity;
import com.example.lunalash.exception.ResourceNotFoundException;
import com.example.lunalash.repository.AvailableDateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class AvailableDateService {

    private final AvailableDateRepository repo;

    public AvailableDateService(AvailableDateRepository repo) {
        this.repo = repo;
    }

    // 訪客月曆用：只回傳有被管理者設定過的日期，沒設定過的一律視為未開放
    public List<CalendarDayResponse> getCalendarForMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        List<AvailableDateEntity> dates = repo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(
                yearMonth.atDay(1), yearMonth.atEndOfMonth());
        return dates.stream().map(CalendarDayResponse::new).toList();
    }

    // 後台管理用：列出所有設定過的日期
    public List<CalendarDayResponse> getAllDates() {
        return repo.findAllByOrderByAppointmentDateAsc().stream().map(CalendarDayResponse::new).toList();
    }

    // 新增/重新開放一個可預約日期 (若已存在就直接改成開放，保持等冪)
    public CalendarDayResponse addDate(AvailableDateRequest request) {
        AvailableDateEntity entity = repo.findByAppointmentDate(request.getDate())
                .orElseGet(() -> {
                    AvailableDateEntity newEntity = new AvailableDateEntity();
                    newEntity.setAppointmentDate(request.getDate());
                    return newEntity;
                });
        entity.setIsOpen(true);
        return new CalendarDayResponse(repo.save(entity));
    }

    public CalendarDayResponse setOpen(Long availableDateId, boolean isOpen) {
        AvailableDateEntity entity = repo.findById(availableDateId)
                .orElseThrow(() -> new ResourceNotFoundException("查無此日期設定"));
        entity.setIsOpen(isOpen);
        return new CalendarDayResponse(repo.save(entity));
    }

    public void deleteDate(Long availableDateId) {
        AvailableDateEntity entity = repo.findById(availableDateId)
                .orElseThrow(() -> new ResourceNotFoundException("查無此日期設定"));
        repo.delete(entity);
    }

    // 給 AvailableSlotService 內部使用：確認這天有沒有開放
    boolean isDateOpen(LocalDate date) {
        return repo.findByAppointmentDate(date).map(AvailableDateEntity::getIsOpen).orElse(false);
    }
}
