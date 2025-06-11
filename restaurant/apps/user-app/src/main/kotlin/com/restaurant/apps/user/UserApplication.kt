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
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(
    exclude = [
        SecurityAutoConfiguration::class,
        UserDetailsServiceAutoConfiguration::class,
        ManagementWebSecurityAutoConfiguration::class,
    ],
)
// Scan components in config, user.presentation, user.application, user.infra, outbox
@ComponentScan(
    basePackages = [
        "com.restaurant.common.presentation",
        "com.restaurant.common.infrastructure.config",
        "com.restaurant.user.presentation",
        "com.restaurant.user.application",
        "com.restaurant.user.infrastructure",
        "com.restaurant.apps.user.config",
        "com.restaurant.outbox.application",
        "com.restaurant.outbox.infrastructure",
    ],
)
@EnableJpaRepositories(
    basePackages = [
        "com.restaurant.user.infrastructure.repository",
        "com.restaurant.outbox.infrastructure.repository",
    ],
)
@EntityScan(
    basePackages = [
        "com.restaurant.user.infrastructure.entity",
        "com.restaurant.outbox.infrastructure.entity",
    ],
)
@EnableScheduling // Outbox Poller 활성화
open class UserApplication

fun main(args: Array<String>) {
    runApplication<UserApplication>(*args)
}
