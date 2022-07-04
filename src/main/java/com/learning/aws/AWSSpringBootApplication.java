package com.learning.aws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.learning.aws")
public class AWSSpringBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(AWSSpringBootApplication.class, args);
    }
}

