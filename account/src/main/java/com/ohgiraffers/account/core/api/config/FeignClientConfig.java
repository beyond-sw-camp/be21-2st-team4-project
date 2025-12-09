package com.ohgiraffers.account.core.api.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {
    @Bean
    public RequestInterceptor headerInterceptor(HttpServletRequest request) {
        return template -> {
            String userId = request.getHeader("X-User-Id");
            String role = request.getHeader("X-User-Role");
            if(userId != null) template.header("X-User-Id", userId);
            if(role != null) template.header("X-User-Role", role);
        };
    }
}

