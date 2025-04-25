package com.restaurant.apps.account.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * JPA 설정
 */
@Configuration
@EntityScan(basePackages = ["com.restaurant.infrastructure.account.entity"])
@EnableJpaRepositories(basePackages = ["com.restaurant.infrastructure.account.repository"])
class JpaConfig
