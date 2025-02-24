package com.ddd.restaurant.application.dto.command

import java.util.UUID

data class ChangeRestaurantInfoCommandDto(
        val restaurantId: UUID,
        val newName: String?,
        val newAddress: RestaurantAddressDto?
) {
        data class RestaurantAddressDto(val street: String, val city: String, val zipCode: String)
}
