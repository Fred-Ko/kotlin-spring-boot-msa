package com.ddd.restaurant.infrastructure.persistence.repository

import com.ddd.restaurant.domain.model.aggregate.Restaurant
import com.ddd.restaurant.domain.repository.RestaurantRepository
import com.ddd.restaurant.infrastructure.persistence.entity.RestaurantEntity
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
class RestaurantRepositoryImpl(private val restaurantJpaRepository: RestaurantJpaRepository) :
        RestaurantRepository {
    override fun save(restaurant: Restaurant): Restaurant {
        val entity = RestaurantEntity.from(restaurant)
        return restaurantJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: UUID): Restaurant? {
        return restaurantJpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findAll(): List<Restaurant> {
        return restaurantJpaRepository.findAll().map { it.toDomain() }
    }

    override fun findAll(page: Int, size: Int): List<Restaurant> {
        // 여기서는 간단하게 하기 위해 findAll()을 사용하고, application layer에서 페이징 처리
        // 실제로는 JpaRepository에서 제공하는 Pageable을 사용해서 구현해야 함
        return findAll().drop((page - 1) * size).take(size)
    }
}

interface RestaurantJpaRepository : JpaRepository<RestaurantEntity, UUID>
