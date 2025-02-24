package com.ddd.restaurant.application.query

import com.ddd.restaurant.application.dto.result.RestaurantListResult

interface FindRestaurantListQuery {
    fun findRestaurantList(page: Int, size: Int): com.ddd.restaurant.application.dto.query.RestaurantListResult
}
