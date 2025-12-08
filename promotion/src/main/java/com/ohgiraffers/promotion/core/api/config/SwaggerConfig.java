package com.ohgiraffers.promotion.core.api.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI PromoiotnOpenAPI() {
        return new OpenAPI().info(swaggerInfo());
    }

    private Info swaggerInfo() {
        return new Info()
                .title("Timedeal Promotion API")
                .description("Timedeal Promotion Swagger 연동 테스트")
                .version("v1.0");
    }
}
