package com.ramennsama.springboot.lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class SpringBootLearningManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootLearningManagementSystemApplication.class, args);
    }

}
