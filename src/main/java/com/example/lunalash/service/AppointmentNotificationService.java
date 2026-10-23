package com.example.lunalash.service;

import com.example.lunalash.entity.AppointmentEntity;
import com.example.lunalash.entity.OperationCatalogItemEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collectors;

// NotificationService 的實作：依這筆預約有哪些身分資料，分別走對應的通知管道。
// - LINE 部分還沒有串接 LINE Login / LIFF / Messaging API，所以還是只先記錄 log，不會真的發送訊息；
//   之後要接 LINE 時，只需要把 logLineStub(...) 換成實際呼叫 LINE Messaging API 的 HTTP 請求即可。
// - Email 部分是真的會寄出去 (透過 EmailService / Gmail SMTP)，只有訪客用 Google 登入建立的預約
//   (appointment.customerEmail 不為 null) 才會收到信，匿名預約不受影響。
// 兩種管道互相獨立，呼叫端 (AppointmentService、AppointmentReminderScheduler) 完全不需要修改。
@Service
public class AppointmentNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentNotificationService.class);

    private final EmailService emailService;

    public AppointmentNotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendBookingReceived(AppointmentEntity appointment) {
        logLineStub(appointment, "已收到預約申請");
        sendEmail(appointment, "已收到您的預約申請",
                "已收到您的預約申請，待店家確認後即可查詢結果。\n\n" + appointmentSummary(appointment)
                        + "\n查詢碼：" + appointment.getQueryCode());
    }

    @Override
    public void sendApproved(AppointmentEntity appointment) {
        logLineStub(appointment, "預約已核准");
        sendEmail(appointment, "您的預約已核准",
                "您的預約已核准，請準時前往。\n\n" + appointmentSummary(appointment));
    }

    @Override
    public void sendRejected(AppointmentEntity appointment) {
        logLineStub(appointment, "預約已拒絕");
        sendEmail(appointment, "您的預約申請未成功",
                "很抱歉，您的預約申請未能受理，歡迎重新選擇時段預約。\n\n" + appointmentSummary(appointment));
    }

    @Override
    public void sendReminder(AppointmentEntity appointment) {
        logLineStub(appointment, "預約前一天提醒");
        sendEmail(appointment, "明天的預約提醒",
                "提醒您，明天有一筆預約，請準時前往。\n\n" + appointmentSummary(appointment));
    }

    @Override
    public void sendRescheduled(AppointmentEntity appointment, LocalDate oldDate, LocalTime oldTime) {
        String eventName = String.format("預約時間已調整（%s %s -> %s %s）",
                oldDate, oldTime, appointment.getAppointmentDate(), appointment.getAppointmentTime());
        logLineStub(appointment, eventName);
        sendEmail(appointment, "您的預約時間已調整",
                "您的預約時間已調整為：\n\n" + appointmentSummary(appointment)
                        + "\n（原時間：" + oldDate + " " + oldTime + "）");
    }

    private void sendEmail(AppointmentEntity appointment, String subject, String text) {
        if (appointment.getCustomerEmail() == null) {
            return;
        }
        emailService.send(appointment.getCustomerEmail(), subject, text);
    }

    private String appointmentSummary(AppointmentEntity appointment) {
        String itemNames = appointment.getOperationItems().stream()
                .map(OperationCatalogItemEntity::getName)
                .collect(Collectors.joining("、"));
        return "操作項目：" + itemNames
                + "\n日期：" + appointment.getAppointmentDate()
                + "\n時間：" + appointment.getAppointmentTime();
    }

    private void logLineStub(AppointmentEntity appointment, String eventName) {
        if (appointment.getLineUserId() == null) {
            log.info("[LINE 通知-尚未串接] {}：預約 #{}（{} {}）目前沒有 lineUserId，之後串接 LINE Login/LIFF 後才有辦法主動推播",
                    eventName, appointment.getAppointmentId(), appointment.getAppointmentDate(), appointment.getAppointmentTime());
            return;
        }
        log.info("[LINE 通知-尚未串接] {}：預約 #{}，lineUserId={}", eventName, appointment.getAppointmentId(), appointment.getLineUserId());
    }
}
