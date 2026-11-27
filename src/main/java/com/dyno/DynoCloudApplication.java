package com.dyno;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DynoCloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(DynoCloudApplication.class, args);
	}

}
