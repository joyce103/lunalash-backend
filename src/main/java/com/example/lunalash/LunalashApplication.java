package com.example.lunalash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 讓 AppointmentReminderScheduler 的 @Scheduled 排程可以運作
public class LunalashApplication {

	public static void main(String[] args) {
		SpringApplication.run(LunalashApplication.class, args);
	}

}
