package com.ljakovic.simpleproducts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class SimpleProductsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleProductsApplication.class, args);
	}

}
