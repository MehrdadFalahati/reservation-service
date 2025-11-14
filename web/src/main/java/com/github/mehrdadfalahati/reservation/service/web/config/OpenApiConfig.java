package com.github.mehrdadfalahati.reservation.service.web.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI reservationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Reservation Service API")
                        .description("""
                                APIs for managing user reservations, enforcing nearest-slot allocation,
                                and ensuring concurrency-safe booking workflows.
                                """)
                        .version("1.0")
                        .contact(new Contact()
                                .name("Reservation Platform")
                                .email("m.falahati2022@gmail.com")))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}
