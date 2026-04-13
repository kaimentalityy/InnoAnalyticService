package com.innowise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class InnoAnalyticServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InnoAnalyticServiceApplication.class, args);
    }

}
