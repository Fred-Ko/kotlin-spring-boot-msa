package com.restaurant.apps.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan(
    basePackages = [
        "com.restaurant.apps.user", // 앱 자체 설정 (기존 앱 패키지 유지)
        "com.restaurant.presentation.user", // Presentation Layer
        "com.restaurant.application.user", // Application Layer
        "com.restaurant.infrastructure.user", // User Infrastructure Layer
        "com.restaurant.independent.outbox", // Outbox Module (전체 스캔 또는 필요한 컴포넌트 패키지 명시)
        "com.restaurant.config", // Global Exception Handler 등 공통 설정 위치 (예: 루트 config 모듈)
    ],
)
@EntityScan(
    basePackages = [
        "com.restaurant.infrastructure.user.entity",
        "com.restaurant.independent.outbox.infrastructure.entity",
    ],
)
@EnableJpaRepositories(
    basePackages = [
        "com.restaurant.infrastructure.user.repository",
        "com.restaurant.independent.outbox.infrastructure.persistence",
    ],
)
class UserApplication

fun main(args: Array<String>) {
    runApplication<UserApplication>(*args)
}
