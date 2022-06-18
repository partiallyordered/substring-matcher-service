package com.example.substring_matcher_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.Banner;

@SpringBootApplication
public class StringMatcherServiceApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(StringMatcherServiceApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

}
