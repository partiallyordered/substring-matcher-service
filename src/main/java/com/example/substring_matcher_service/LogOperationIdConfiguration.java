package com.example.substring_matcher_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Configuration
public class LogOperationIdConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.myLogInterceptor());
    }

    @Bean
    public LogOperationIdInterceptor myLogInterceptor() {
        return new LogOperationIdInterceptor();
    }
}
