package com.sn.tonux.mail.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MailBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(MailBatchApplication.class, args);
	}
}
