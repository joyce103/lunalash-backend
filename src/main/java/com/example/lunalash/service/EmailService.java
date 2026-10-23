package com.example.lunalash.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

// 只負責「把信寄出去」這個 transport 層，不管信件內容怎麼組；
// 內容/各種通知情境的文字由呼叫端 (AppointmentNotificationService) 決定
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromName;

    public EmailService(JavaMailSender mailSender, @Value("${mail.from-name}") String fromName) {
        this.mailSender = mailSender;
        this.fromName = fromName;
    }

    // 寄信失敗 (帳密錯誤、SMTP 連不上等) 只記 log，不往外拋例外，
    // 避免通知信寄失敗連帶讓「送出預約」「核准預約」這些真正的業務操作失敗
    public void send(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromName);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("通知信已寄出：to={}, subject={}", to, subject);
        } catch (MailException e) {
            log.warn("通知信寄送失敗：to={}, subject={}, error={}", to, subject, e.getMessage());
        }
    }
}
