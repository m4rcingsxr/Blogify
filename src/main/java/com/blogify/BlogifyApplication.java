package com.blogify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
		scanBasePackages={"com.blogify"}
)
public class BlogifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogifyApplication.class, args);
	}

}
