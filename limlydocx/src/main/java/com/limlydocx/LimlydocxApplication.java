package com.limlydocx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:security.properties")
public class LimlydocxApplication {

	public static void main(String[] args) {
		SpringApplication.run(LimlydocxApplication.class, args);
	}

}