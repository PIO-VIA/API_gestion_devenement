package com.project.POO.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SwaggerConfig {

    @Bean
    @Primary
    public OpenAPI eventManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Système de Gestion d'Événements")
                        .description("API REST pour la gestion d'événements, participants, notifications, etc.")
                        .version("1.0")
                        .contact(new Contact()
                                .name("PIO")
                                .email("piodjiele@gmail.com")
                                .url("https://github.com/ton-projet"))
                        .license(new License()
                                .name("MIT License")));
    }
}
