package com.ddd.restaurant.domain.repository

import com.ddd.restaurant.domain.model.aggregate.Restaurant
import java.util.*

interface RestaurantRepository {
    fun save(restaurant: Restaurant): Restaurant
    fun findById(id: UUID): Restaurant?
    fun findAll(page: Int, size: Int): List<Restaurant>
}
