package com.helloworld.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@GetMapping("/get")
	public String hello() {
		return "Hello, World! This is my first Spring Boot application CI/CD Pipeline. I added a webhook to trigger build and deployment.";
	}

	@GetMapping("/helloworld")
	public String helloworlod() {
		return "Hello, World! This is my first Spring Boot application CI/CD Pipeline resolve error./n I added a webhook to trigger build and deployment.";
	}

	@GetMapping("/update")
	public String update() {
		return "updated line | added new function in GETMapping.";
	}
}
