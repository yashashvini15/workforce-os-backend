package com.workforce.ai.authservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                    .title("AI Workforce OS - Auth API")
                    .version("1.0")
                    .description("APIs for Authentication, MFA (OTP), and Password Reset"))
                .addServersItem(new io.swagger.v3.oas.models.servers.Server()
                        .url("https://workforce-os-backend-production.up.railway.app"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components( new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
