package com.maket.maket_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class MaketBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaketBackendApplication.class, args);
	}

}
