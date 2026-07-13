package com.example.lunalash.service;

import com.example.lunalash.entity.AppointmentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

// NotificationService 的 LINE 存根實作。
// 目前還沒有串接 LINE Login / LIFF / Messaging API，所以每個方法只先記錄 log，不會真的發送訊息。
// 之後要接 LINE 時，只需要：
//   1. 在這裡注入 Channel Access Token (例如透過 @Value("${line.channel-access-token}"))
//   2. 把 log.info(...) 換成實際呼叫 LINE Messaging API 的 HTTP 請求
// 呼叫端 (AppointmentService、AppointmentReminderScheduler) 完全不需要修改。
@Service
public class LineNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(LineNotificationService.class);

    @Override
    public void sendBookingReceived(AppointmentEntity appointment) {
        logStub(appointment, "已收到預約申請");
    }

    @Override
    public void sendApproved(AppointmentEntity appointment) {
        logStub(appointment, "預約已核准");
    }

    @Override
    public void sendRejected(AppointmentEntity appointment) {
        logStub(appointment, "預約已拒絕");
    }

    @Override
    public void sendReminder(AppointmentEntity appointment) {
        logStub(appointment, "預約前一天提醒");
    }

    private void logStub(AppointmentEntity appointment, String eventName) {
        if (appointment.getLineUserId() == null) {
            log.info("[LINE 通知-尚未串接] {}：預約 #{}（{} {}）目前沒有 lineUserId，之後串接 LINE Login/LIFF 後才有辦法主動推播",
                    eventName, appointment.getAppointmentId(), appointment.getAppointmentDate(), appointment.getAppointmentTime());
            return;
        }
        log.info("[LINE 通知-尚未串接] {}：預約 #{}，lineUserId={}", eventName, appointment.getAppointmentId(), appointment.getLineUserId());
    }
}
