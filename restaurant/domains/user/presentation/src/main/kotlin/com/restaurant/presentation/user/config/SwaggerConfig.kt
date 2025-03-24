package com.restaurant.presentation.user.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
                .addServersItem(Server().url("/").description("현재 서버"))
                .info(
                        Info().title("레스토랑 서비스 API")
                                .version("v1")
                                .description("레스토랑 서비스의 RESTful API 문서")
                                .contact(Contact().name("개발팀").email("dev@restaurant.com"))
                )
                .components(
                        Components()
                                .addSecuritySchemes(
                                        "bearer-jwt",
                                        SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description("JWT 토큰을 헤더에 입력하세요")
                                )
                )
    }

    @Bean
    fun userApiV1(): GroupedOpenApi {
        return GroupedOpenApi.builder()
                .group("user-api-v1")
                .pathsToMatch("/api/v1/users/**")
                .packagesToScan("com.restaurant.presentation.user.v1")
                .build()
    }
}
