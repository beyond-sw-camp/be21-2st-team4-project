package com.ohgiraffers.timedeal.core.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS(Cross-Origin Resource Sharing) 설정
 * 프론트엔드(localhost:5173)에서 백엔드(localhost:8080) API 접근 허용
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // /api로 시작하는 모든 경로에 대해
                .allowedOrigins(
                        "http://localhost:5173",  // Vite 개발 서버
                        "http://localhost:3000"   // 추가 개발 서버 (필요시)
                )
                .allowedMethods(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
                .allowedHeaders("*")  // 모든 헤더 허용
                .allowCredentials(true)  // 쿠키/세션 허용
                .maxAge(3600);  // pre-flight 요청 캐시 시간 (1시간)
    }
}
