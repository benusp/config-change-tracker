package com.example.config_change_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class ConfigChangeTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigChangeTrackerApplication.class, args);
	}

}
