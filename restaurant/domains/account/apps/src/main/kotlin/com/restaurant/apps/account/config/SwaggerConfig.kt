package com.restaurant.apps.account.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Swagger(OpenAPI) 설정
 */
@Configuration
class SwaggerConfig {
    @Bean
    fun accountApi(): GroupedOpenApi =
        GroupedOpenApi
            .builder()
            .group("account-api")
            .pathsToMatch("/api/v1/accounts/**")
            .build()

    @Bean
    fun openAPI(): OpenAPI =
        OpenAPI()
            .info(apiInfo())
            .servers(
                listOf(
                    Server().url("/").description("Default Server URL"),
                ),
            ).components(Components())

    private fun apiInfo(): Info =
        Info()
            .title("계좌 관리 API")
            .description("계좌 등록, 결제 처리, 잔액 조회 등 계좌 관련 API")
            .version("v1")
            .contact(
                Contact()
                    .name("Restaurant MSA Team")
                    .email("api@restaurant.com")
                    .url("https://restaurant.com"),
            ).license(
                License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html"),
            )
}
