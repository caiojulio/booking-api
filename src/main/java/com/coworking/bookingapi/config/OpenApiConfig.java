package com.coworking.bookingapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração da documentação viva da API utilizando o OpenAPI (Swagger).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Coworking Booking API")
                        .version("v1.0.0")
                        .description("API REST para gestão de salas e reservas num espaço de coworking. " +
                                "O sistema garante a prevenção de conflitos de horários e a gestão da agenda diária.")
                        .contact(new Contact()
                                .name("Caio Silva")
                                .email("caiojuliosilv@gmail.com")
                                .url("https://github.com/caiojulio/booking-api")));
    }
}