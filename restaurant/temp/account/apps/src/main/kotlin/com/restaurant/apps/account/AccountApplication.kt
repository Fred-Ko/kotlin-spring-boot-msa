package com.restaurant.apps.account

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * 계좌 마이크로서비스 진입점
 */
@SpringBootApplication(
    scanBasePackages = [
        "com.restaurant.apps.account",
        "com.restaurant.presentation.account",
        "com.restaurant.application.account",
        "com.restaurant.infrastructure.account",
        "com.restaurant.domain.account",
        "com.restaurant.common",
    ],
)
class AccountApplication

/**
 * 애플리케이션 시작점
 */
fun main(args: Array<String>) {
    runApplication<AccountApplication>(*args)
}
