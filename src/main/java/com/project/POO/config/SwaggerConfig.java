package com.project.POO.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI eventManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Système de Gestion d'Événements")
                        .description("API REST pour la gestion d'événements, participants, notifications, etc.")
                        .version("1.0")
                        .contact(new Contact()
                                .name("PIO")
                                .email("piodjiele@gmail.com")
                                .url("https://github.com/PIO-VIA/API_gestion_devenement.git"))
                        .license(new License()
                                .name("MIT License")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**")
                .build();
    }
}