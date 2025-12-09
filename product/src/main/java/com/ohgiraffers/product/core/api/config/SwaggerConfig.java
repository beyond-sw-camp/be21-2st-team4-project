package com.ohgiraffers.product.core.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        Info info = new Info()
                .title("Product Service API")
                .description("Timedeal Product Service API")
                .version("1.0.0");

        String jwtSchemeName = "jwtAuth";

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(jwtSchemeName);

        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName,
                        new SecurityScheme()
                                .name(jwtSchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                );

        // Gateway를 통한 API 호출을 위한 서버 설정
        Server gatewayServer = new Server()
                .url("http://localhost:8000/api/v1/products")
                .description("Gateway Server");

        Server localServer = new Server()
                .url("/")
                .description("Direct Access");

        return new OpenAPI()
                .info(info)
                .servers(List.of(gatewayServer, localServer))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
