package com.ddd.restaurant.infrastructure.persistence.repository

import com.ddd.restaurant.domain.model.aggregate.Restaurant
import com.ddd.restaurant.domain.port.repository.RestaurantRepository
import com.ddd.restaurant.infrastructure.persistence.entity.RestaurantJpaEntity
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class RestaurantRepositoryImpl(private val restaurantJpaRepository: RestaurantJpaRepository) :
        RestaurantRepository {

    override fun save(restaurant: Restaurant): Restaurant {
        val restaurantJpaEntity = RestaurantJpaEntity.fromDomain(restaurant)
        return restaurantJpaRepository.save(restaurantJpaEntity).toDomain()
    }

    override fun findById(id: UUID): Restaurant? {
        return restaurantJpaRepository.findById(id).map { it.toDomain() }.orElse(null)
    }

    override fun findAll(pageable: Pageable): Page<Restaurant> {
        return restaurantJpaRepository.findAll(pageable).map { it.toDomain() }
    }

    override fun deleteById(id: UUID) {
        restaurantJpaRepository.deleteById(id)
    }
}
