package com.restaurant.payment.infrastructure.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * Payment 도메인 JPA 설정 (Rule 141.5)
 */
@Configuration
@EnableJpaRepositories(basePackages = ["com.restaurant.payment.infrastructure.repository"])
@EntityScan(basePackages = ["com.restaurant.payment.infrastructure.entity"])
@EnableTransactionManagement
class JpaConfig
