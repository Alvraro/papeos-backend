package com.alvraro.papeos_backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PapeosBackendApplication {

	private static final Logger logger = LoggerFactory.getLogger(PapeosBackendApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(PapeosBackendApplication.class, args);
	}
}
