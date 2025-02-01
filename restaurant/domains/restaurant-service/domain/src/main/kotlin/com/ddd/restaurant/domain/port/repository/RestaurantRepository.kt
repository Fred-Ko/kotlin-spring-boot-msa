package com.ddd.restaurant.domain.port.repository

import com.ddd.restaurant.domain.model.aggregate.Restaurant
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface RestaurantRepository {
    fun save(restaurant: Restaurant): Restaurant
    fun findById(id: UUID): Restaurant?
    fun findAll(pageable: Pageable): Page<Restaurant>
    fun deleteById(id: UUID)
}
