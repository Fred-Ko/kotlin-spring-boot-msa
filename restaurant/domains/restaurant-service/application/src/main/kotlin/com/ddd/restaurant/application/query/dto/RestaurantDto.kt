package com.ddd.restaurant.application.query.dto

import com.ddd.restaurant.domain.model.aggregate.Restaurant
import java.util.UUID

data class RestaurantDto(val id: UUID, val name: String, val status: String) {
    companion object {
        fun from(restaurant: Restaurant) =
                RestaurantDto(
                        id = restaurant.id,
                        name = restaurant.name.value,
                        status = restaurant.status.toString()
                )
    }
}
