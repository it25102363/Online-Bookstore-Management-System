package com.WD30Final.BookStore_ManagementSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class BookStoreManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookStoreManagementSystemApplication.class, args);
	}

}
