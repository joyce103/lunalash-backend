package com.example.lunalash.service;

import com.example.lunalash.entity.AppointmentEntity;

// 預約狀態一律透過 LINE 官方帳號通知客人，目前先只保留介面與存根實作 (LineNotificationService)，
// 之後只要把 LINE Messaging API 的 Channel Access Token 接上、實作這幾個方法即可，
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
}
