package com.source.bundleboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        Redis
})
public class BundleboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(BundleboardApplication.class, args);
	}

}
