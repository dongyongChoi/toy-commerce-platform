package com.toyproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CommerceApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommerceApiApplication.class, args);
    }
}

