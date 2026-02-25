package com.source.bundleboard;

import org.springframework.boot.SpringApplication;

public class TestBundleboardApplication {

	public static void main(String[] args) {
		SpringApplication.from(BundleboardApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
