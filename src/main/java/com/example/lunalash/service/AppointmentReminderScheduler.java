package com.example.lunalash.service;

import com.example.lunalash.entity.AppointmentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

// 每天固定時間執行一次，找出「明天」所有已核准的預約，呼叫 NotificationService 發提醒通知。
// 目前 NotificationService 是 LINE 存根實作，還不會真的發送，但排程架構已經完整保留，
// 之後 LINE Messaging API 串接好後，這裡完全不需要修改。
@Component
public class AppointmentReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(AppointmentReminderScheduler.class);

    private final AppointmentService appointmentService;
    private final NotificationService notificationService;

    public AppointmentReminderScheduler(AppointmentService appointmentService, NotificationService notificationService) {
        this.appointmentService = appointmentService;
        this.notificationService = notificationService;
    }

    // 每天早上 9:00 執行一次
    @Scheduled(cron = "0 0 9 * * *")
    public void sendTomorrowReminders() {
        List<AppointmentEntity> appointments = appointmentService.findTomorrowApprovedAppointments();
        log.info("開始發送明日預約提醒，共 {} 筆", appointments.size());
        for (AppointmentEntity appointment : appointments) {
            notificationService.sendReminder(appointment);
        }
    }
}
