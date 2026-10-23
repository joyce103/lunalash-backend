package com.example.lunalash.service;

import com.example.lunalash.entity.AppointmentEntity;

import java.time.LocalDate;
import java.time.LocalTime;

// 預約狀態通知，實作見 AppointmentNotificationService：
// Email 部分已經接上 Gmail SMTP 真的會寄信 (只有 appointment.customerEmail 不為 null 時才會寄)，
// LINE 部分還只是存根，等之後把 LINE Messaging API 的 Channel Access Token 接上即可，
// 不需要改動呼叫端 (AppointmentService / AppointmentReminderScheduler)
public interface NotificationService {

    // 收到新預約申請時通知客人
    void sendBookingReceived(AppointmentEntity appointment);

    // 預約被核准時通知客人
    void sendApproved(AppointmentEntity appointment);

    // 預約被拒絕時通知客人
    void sendRejected(AppointmentEntity appointment);

    // 預約前一天的提醒通知
    void sendReminder(AppointmentEntity appointment);

    // 後台調整預約時間後通知客人，附上改期前的日期時間
    void sendRescheduled(AppointmentEntity appointment, LocalDate oldDate, LocalTime oldTime);
}
