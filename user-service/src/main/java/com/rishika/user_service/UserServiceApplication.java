package com.rishika.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class UserServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(UserServiceApplication.class, args);

		System.out.println("UserService server is running...");
	}

}
