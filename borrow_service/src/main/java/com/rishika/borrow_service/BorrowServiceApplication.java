package com.rishika.borrow_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BorrowServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(BorrowServiceApplication.class, args);
		System.out.println("Borrow Service is running");
	}

}
