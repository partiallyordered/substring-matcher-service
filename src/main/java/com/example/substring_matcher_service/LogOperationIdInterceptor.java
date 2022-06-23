package com.example.substring_matcher_service;

import java.util.UUID;

import org.springframework.web.servlet.HandlerInterceptor;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;

@Component
public class LogOperationIdInterceptor implements HandlerInterceptor {
    private static final Logger logger = LogManager.getLogger(LogOperationIdInterceptor.class);

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) {
        String operationId = UUID.randomUUID().toString();
        ThreadContext.put("operationId", operationId);
        logger.info("Received " + request.getMethod() + " " + request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception exception) throws Exception {
        logger.info("Handled " + request.getMethod() + " " + request.getRequestURI());
        ThreadContext.clearMap();
    }
}
