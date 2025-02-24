package com.ddd.restaurant.application.dto.result

import java.util.UUID

data class RestaurantListResult(val restaurants: List<RestaurantInfo>) {
    data class RestaurantInfo(
            val id: UUID,
            val name: String,
            val address: Address,
            val status: RestaurantStatus
    ) {
        data class Address(val street: String, val city: String, val zipCode: String)

        enum class RestaurantStatus {
            OPEN,
            CLOSED
        }
    }
}
