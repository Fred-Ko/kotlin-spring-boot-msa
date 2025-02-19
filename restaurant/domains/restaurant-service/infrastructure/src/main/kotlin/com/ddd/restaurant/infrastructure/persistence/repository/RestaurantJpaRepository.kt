package com.ddd.restaurant.infrastructure.persistence.repository

import com.ddd.restaurant.infrastructure.persistence.entity.RestaurantJpaEntity
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface RestaurantJpaRepository : JpaRepository<RestaurantJpaEntity, UUID>
