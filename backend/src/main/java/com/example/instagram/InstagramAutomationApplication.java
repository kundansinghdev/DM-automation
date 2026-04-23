package com.example.instagram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class InstagramAutomationApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstagramAutomationApplication.class, args);
		log.info("----------------------------------------------------------");
		log.info("   Instagram Comment-to-DM Automation Started Successfully");
		log.info("   Backend: http://localhost:8000");
		log.info("----------------------------------------------------------");
	}

}
