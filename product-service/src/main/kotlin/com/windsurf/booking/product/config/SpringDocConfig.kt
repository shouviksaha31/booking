package com.windsurf.booking.product.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpringDocConfig {

    @Bean
    fun springOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Flight Booking Platform - Product Service API")
                    .description("API for the Product Service of the Flight Booking Platform")
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("Windsurf Engineering Team")
                            .email("engineering@windsurf.com")
                            .url("https://windsurf.com")
                    )
                    .license(
                        License()
                            .name("Proprietary")
                            .url("https://windsurf.com/terms")
                    )
            )
            .servers(
                listOf(
                    Server().url("http://localhost:8080").description("Local development server"),
                    Server().url("https://api.booking.windsurf.com").description("Production server")
                )
            )
    }
}
