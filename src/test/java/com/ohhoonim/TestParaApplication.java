package com.ohhoonim;

import org.springframework.boot.SpringApplication;

public class TestParaApplication {

	public static void main(String[] args) {
		SpringApplication.from(ParaApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
