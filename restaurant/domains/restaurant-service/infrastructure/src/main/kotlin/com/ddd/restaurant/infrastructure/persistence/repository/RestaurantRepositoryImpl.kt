package com.ddd.restaurant.infrastructure.persistence.repository

import com.ddd.restaurant.domain.model.aggregate.Restaurant
import com.ddd.restaurant.domain.repository.RestaurantRepository
import com.ddd.restaurant.infrastructure.persistence.entity.RestaurantJpaEntity
import org.springframework.data.domain.Pageable
import java.util.*
import org.springframework.stereotype.Repository

@Repository
class RestaurantRepositoryImpl(private val restaurantJpaRepository: RestaurantJpaRepository) :
        RestaurantRepository {

    override fun save(restaurant: Restaurant): Restaurant {
        val entity = RestaurantJpaEntity.from(restaurant)
        return restaurantJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: UUID): Restaurant? {
        return restaurantJpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findAll(page: Int, size: Int): List<Restaurant> {
        return restaurantJpaRepository.findAll(Pageable.ofSize(size).withPage(page)).content.map { it.toDomain() }
    }
}