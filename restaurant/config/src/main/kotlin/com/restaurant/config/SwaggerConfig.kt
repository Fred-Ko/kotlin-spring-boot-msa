package com.restaurant.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Value("\${springdoc.server.url:http://localhost:8080}")
    private lateinit var serverUrl: String

    @Value("\${spring.application.name:API}")
    private lateinit var applicationName: String

    @Value("\${spring.application.description:API Documentation}")
    private lateinit var applicationDescription: String

    @Value("\${spring.application.version:1.0.0}")
    private lateinit var applicationVersion: String

    @Bean
    fun openAPI(): OpenAPI {
        val info =
            Info()
                .title(applicationName)
                .description(applicationDescription)
                .version(applicationVersion)

        // Define security scheme (e.g., JWT Bearer)
        val jwtSchemeName = "bearerAuth"
        val securityScheme =
            SecurityScheme()
                .name(jwtSchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")

        return OpenAPI()
            .info(info)
            .servers(listOf(Server().url(serverUrl).description("Default Server URL")))
            .components(Components().addSecuritySchemes(jwtSchemeName, securityScheme))
            .addSecurityItem(SecurityRequirement().addList(jwtSchemeName))
    }
}
