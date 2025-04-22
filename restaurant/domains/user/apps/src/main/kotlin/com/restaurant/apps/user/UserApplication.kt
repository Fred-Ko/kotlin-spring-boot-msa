package com.restaurant.apps.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan(
    basePackages = [
        "com.restaurant.apps.user", // 앱 설정
        "com.restaurant.presentation.user", // 컨트롤러 등
        "com.restaurant.application.user", // 서비스 핸들러
        "com.restaurant.infrastructure.user", // 리포지토리 구현체 등 (JPA Repo 제외)
        "com.restaurant.common.presentation", // GlobalExceptionHandler 등
        // Outbox 모듈의 특정 컴포넌트가 필요하다면 더 구체적으로 명시
        "com.restaurant.independent.outbox.application", // OutboxPoller 등
        "com.restaurant.independent.outbox.infrastructure.kafka", // OutboxMessageSender 등
        "com.restaurant.independent.outbox.infrastructure.config", // Outbox Kafka 설정 등
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
