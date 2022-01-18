package com.example.projectfirst;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class ProjectFirstApplication{

	public static void main(String[] args) {
		SpringApplication.run(ProjectFirstApplication.class, args);
	}
}
