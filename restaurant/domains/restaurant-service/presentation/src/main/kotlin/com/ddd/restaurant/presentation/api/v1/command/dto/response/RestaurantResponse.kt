package com.ddd.restaurant.presentation.api.v1.command.dto.response

class RestaurantResponse {
    data class CreateRestaurantResponse(
            val message: String,
    )

    data class AddRestaurantMenuItemResponse(
            val message: String,
    )

    data class RemoveRestaurantMenuItemResponse(
            val message: String,
    )
}
