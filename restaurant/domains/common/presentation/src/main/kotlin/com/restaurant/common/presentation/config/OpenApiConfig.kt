package com.restaurant.common.presentation.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import io.swagger.v3.oas.models.tags.Tag
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun openAPI(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("Restaurant User Service API")
                    .description(
                        """
                        ## 사용자 관리 서비스 API
                        
                        이 API는 레스토랑 플랫폼의 사용자 관리 기능을 제공합니다.
                        
                        ### 주요 기능
                        - 사용자 회원가입 및 로그인
                        - 사용자 프로필 관리
                        - 사용자 주소 관리
                        - 비밀번호 변경 및 계정 삭제
                        
                        ### 인증 방식
                        JWT Bearer Token을 사용합니다. 로그인 후 받은 accessToken을 Authorization 헤더에 포함시켜 주세요.
                        
                        ### 오류 응답
                        모든 오류 응답은 RFC 9457 ProblemDetail 형식을 따릅니다.
                        
                        ### API 버전 관리
                        - v1: 현재 안정 버전
                        - CQRS 패턴 적용: Command(생성/수정/삭제)와 Query(조회) 분리
                        """.trimIndent(),
                    ).version("1.0.0")
                    .contact(
                        Contact()
                            .name("Restaurant Development Team")
                            .email("dev@restaurant.com")
                            .url("https://restaurant.com"),
                    ).license(
                        License()
                            .name("MIT License")
                            .url("https://opensource.org/licenses/MIT"),
                    ),
            ).servers(
                listOf(
                    Server()
                        .url("http://localhost:8090")
                        .description("개발 서버 (Local)"),
                    Server()
                        .url("https://dev-api.restaurant.com")
                        .description("개발 환경 (Development)"),
                    Server()
                        .url("https://staging-api.restaurant.com")
                        .description("스테이징 환경 (Staging)"),
                    Server()
                        .url("https://api.restaurant.com")
                        .description("운영 환경 (Production)"),
                ),
            ).components(
                Components()
                    .addSecuritySchemes(
                        "bearerAuth",
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("JWT Bearer Token 인증"),
                    ),
            ).security(
                listOf(
                    SecurityRequirement().addList("bearerAuth"),
                ),
            ).tags(
                listOf(
                    Tag().name("Health Check").description("애플리케이션 상태 확인 API"),
                    Tag().name("Simple").description("간단한 테스트 API"),
                    Tag().name("User Commands").description("사용자 계정 관리 API (생성/수정/삭제)"),
                    Tag().name("User Queries").description("사용자 정보 조회 API"),
                    Tag().name("User Address Commands").description("사용자 주소 관리 API (생성/수정/삭제)"),
                    Tag().name("User Address Queries").description("사용자 주소 조회 API"),
                ),
            )
}
