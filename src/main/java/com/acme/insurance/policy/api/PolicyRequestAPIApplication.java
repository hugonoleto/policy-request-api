package com.acme.insurance.policy.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PolicyRequestAPIApplication {

	public static void main(String[] args) {
		SpringApplication.run(PolicyRequestAPIApplication.class, args);
	}

}
