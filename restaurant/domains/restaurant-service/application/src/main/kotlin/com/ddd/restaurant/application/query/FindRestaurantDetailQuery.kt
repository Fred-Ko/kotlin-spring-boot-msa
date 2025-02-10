package com.ddd.restaurant.application.query

import com.ddd.restaurant.application.dto.query.RestaurantDetailResult
import java.util.UUID

interface FindRestaurantDetailQuery {
    fun findRestaurantDetail(restaurantId: UUID): RestaurantDetailResult
}
