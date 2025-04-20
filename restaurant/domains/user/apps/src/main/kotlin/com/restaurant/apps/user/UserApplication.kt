package com.restaurant.apps.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan(
    basePackages = [
        "com.restaurant.apps.user",
        "com.restaurant.presentation.user",
        "com.restaurant.application.user",
        "com.restaurant.infrastructure.user",
        "com.restaurant.common",
        "com.restaurant.shared.outbox",
    ],
)
@EntityScan(
    basePackages = [
        "com.restaurant.infrastructure.user.entity",
        "com.restaurant.shared.outbox.infrastructure.entity",
    ],
)
@EnableJpaRepositories(
    basePackages = [
        "com.restaurant.infrastructure.user.repository",
        "com.restaurant.shared.outbox.infrastructure.persistence",
    ],
)
class UserApplication

fun main(args: Array<String>) {
    runApplication<UserApplication>(*args)
}
