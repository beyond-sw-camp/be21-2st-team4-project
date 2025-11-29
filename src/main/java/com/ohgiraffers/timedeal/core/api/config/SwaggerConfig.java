package com.ohgiraffers.timedeal.core.api.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(swaggerInfo());
    }

    private Info swaggerInfo() {
        return new Info()
                .title("Timedeal API")
                .description("Timedeal Swagger 연동 테스트")
                .version("1.0.0");
    }
}
