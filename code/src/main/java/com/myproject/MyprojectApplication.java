package com.myproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main Spring Boot Application for Shopping Cart Management System
 * 
 * @author Generated
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.myproject.models.repositories")
public class MyprojectApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyprojectApplication.class, args);
    }
}