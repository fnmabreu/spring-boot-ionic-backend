package com.omega.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.omega.backend.services.S3Service;

@SpringBootApplication
public class SpringIonicApplication implements CommandLineRunner {

	@Autowired
	private S3Service s3Service;

	public static void main(String[] args) {
		SpringApplication.run(SpringIonicApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String path = System.getProperty("user.home") + "/Downloads/cat1.jpg";
		s3Service.uploadFile(path);

	}
}
