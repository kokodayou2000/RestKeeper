package com.restkeeper.operator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.restkeeper"})
public class OperatorCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(OperatorCenterApplication.class, args);
    }
}
