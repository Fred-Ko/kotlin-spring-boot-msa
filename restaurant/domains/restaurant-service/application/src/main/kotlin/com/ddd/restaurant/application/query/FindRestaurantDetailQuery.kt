package com.ddd.restaurant.application.query

import com.ddd.restaurant.application.dto.result.RestaurantDetailResult
import java.util.UUID

interface FindRestaurantDetailQuery {
    fun findRestaurantDetail(restaurantId: UUID): com.ddd.restaurant.application.dto.query.RestaurantDetailResult
}
