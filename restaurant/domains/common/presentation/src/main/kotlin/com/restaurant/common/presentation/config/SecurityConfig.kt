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

                authz.requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                authz.requestMatchers(HttpMethod.POST, "/api/v1/users/login").permitAll()

                authz.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                authz.requestMatchers("/actuator/**").permitAll()

                authz.anyRequest().authenticated()
            }

        return http.build()
    }
}
