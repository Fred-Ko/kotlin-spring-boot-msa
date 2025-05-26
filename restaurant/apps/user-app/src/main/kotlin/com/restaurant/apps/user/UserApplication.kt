/**
 * UserApplication entry point for the user service.
 *
 * Configures component scanning and enables scheduling for Outbox Poller.
 *
 * @author junoko
 */
package com.restaurant.apps.user

import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
    exclude = [
        SecurityAutoConfiguration::class,
        UserDetailsServiceAutoConfiguration::class,
        ManagementWebSecurityAutoConfiguration::class,
    ],
)
// Scan components in config, user.presentation, user.application, user.infra (Outbox 제외)
@ComponentScan(
    basePackages = [
        "com.restaurant.common.presentation",
        "com.restaurant.user.presentation",
        "com.restaurant.user.application",
        "com.restaurant.user.infrastructure",
        "com.restaurant.apps.user.config",
        // Outbox 관련 패키지 제거
    ],
)
@EnableJpaRepositories(
    basePackages = [
        "com.restaurant.user.infrastructure.repository",
        // Outbox 관련 Repository 제거
    ],
)
@EntityScan(
    basePackages = [
        "com.restaurant.user.infrastructure.entity",
        // Outbox 관련 Entity 제거
    ],
)
// @EnableScheduling // Outbox Poller 비활성화
open class UserApplication

fun main(args: Array<String>) {
    runApplication<UserApplication>(*args)
}
