package com.restaurant.common.presentation.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { authz ->
                // 최소한의 공개 엔드포인트만 permitAll
                authz.requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                authz.requestMatchers(HttpMethod.POST, "/api/v1/users/login").permitAll()
                authz.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                authz.requestMatchers("/actuator/**").permitAll()
                // 나머지는 인증 필요
                authz.anyRequest().authenticated()
            }
            // JWT 인증 필터를 추가하려면 아래와 같이 확장
            // .addFilterBefore(JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}
