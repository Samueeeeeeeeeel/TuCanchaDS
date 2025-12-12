package com.example.Canchas.config;

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
                        .title("Microservicio de Canchas - API Documentation")
                        .version("1.0.0")
                        .description("API REST para la gestión de canchas deportivas. " +
                                "Permite crear, consultar, actualizar y eliminar canchas, " +
                                "así como buscar por tipo, ciudad y estado de activación.")
                        .contact(new Contact()
                                .name("TuCancha")
                                .email("support@tucancha.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}

