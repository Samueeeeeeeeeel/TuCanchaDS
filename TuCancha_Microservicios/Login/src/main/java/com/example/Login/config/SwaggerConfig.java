package com.example.Login.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de Login y Usuarios - API Documentation")
                        .version("1.0.0")
                        .description("API REST para la gestión de usuarios y autenticación. " +
                                "Permite registro, login, gestión de usuarios y operaciones exclusivas de administrador. " +
                                "Incluye sistema de roles (ADMIN y USUARIO).")
                        .contact(new Contact()
                                .name("TuCancha")
                                .email("support@tucancha.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}

