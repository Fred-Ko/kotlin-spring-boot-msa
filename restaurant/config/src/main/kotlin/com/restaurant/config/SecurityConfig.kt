package com.restaurant.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() } // API 서버이므로 CSRF 비활성화
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // JWT 사용하므로 세션 사용 안함
            .authorizeHttpRequests { authz ->
                // 공개 엔드포인트
                authz.requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                authz.requestMatchers(HttpMethod.POST, "/api/v1/users/login").permitAll()

                // Swagger UI 및 API 문서 접근 허용
                authz.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // 헬스 체크 등 모니터링 엔드포인트
                authz.requestMatchers("/actuator/**").permitAll()

                // 나머지 엔드포인트는 인증 필요
                authz.anyRequest().authenticated()
            }

        // 참고: JWT 필터는 별도 구현 필요 (이 PR에서는 구현하지 않음)
        // 실제 구현 시에는 http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) 추가 필요

        return http.build()
    }
}
