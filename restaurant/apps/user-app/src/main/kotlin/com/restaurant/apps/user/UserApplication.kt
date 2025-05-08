/**
 * UserApplication entry point for the user service.
 *
 * Configures component scanning and enables scheduling for Outbox Poller.
 *
 * @author junoko
 */
package com.restaurant.apps.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling // For Outbox Poller @Scheduled

@SpringBootApplication
// Scan components in config, outbox.infra, user.presentation, user.application, user.infra
@ComponentScan(
    basePackages = [
        "com.restaurant.common.presentation",
        "com.restaurant.user.presentation",
        "com.restaurant.user.application",
        "com.restaurant.user.infrastructure",
        "com.restaurant.outbox.application",
        "com.restaurant.outbox.infrastructure"
    ],
)
// Explicitly enable JPA repositories if not found by default scan
@EnableJpaRepositories(
    basePackages = [
        "com.restaurant.user.infrastructure.persistence.repository",
        "com.restaurant.outbox.infrastructure.persistence"
    ],
)
@EnableScheduling // Enable @Scheduled tasks (for Outbox Poller)
class UserApplication

fun main(args: Array<String>) {
    runApplication<UserApplication>(*args)
}
