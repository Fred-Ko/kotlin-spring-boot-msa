package com.restaurant.apps.user.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
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
            .csrf { it.disable() } // 개발 편의상 CSRF 비활성화 (실제 환경에서는 필요에 따라 설정)
            .authorizeHttpRequests { authz ->
                authz.anyRequest().permitAll() // 개발 편의상 모든 요청 허용 (실제 환경에서는 인증/인가 필요)
            }
        return http.build()
    }
}
