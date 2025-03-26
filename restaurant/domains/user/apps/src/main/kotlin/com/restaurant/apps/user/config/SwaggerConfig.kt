package com.restaurant.apps.user.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
  @Bean
  fun publicApi(): GroupedOpenApi =
    GroupedOpenApi
      .builder()
      .group("user-api-v1")
      .pathsToMatch("/api/v1/users/**")
      .build()

  @Bean
  fun springOpenAPI(): OpenAPI =
    OpenAPI()
      .info(
        Info()
          .title("User Service API")
          .description("User Service API Documentation")
          .version("v1"),
      )
}
