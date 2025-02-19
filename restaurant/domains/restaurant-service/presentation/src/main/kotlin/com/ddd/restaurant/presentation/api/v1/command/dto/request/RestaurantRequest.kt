package com.ddd.restaurant.presentation.api.v1.command.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

class RestaurantRequest {
    data class CreateRestaurantRequest(
            @field:NotBlank val name: String,
            @field:NotBlank val address: String,
            @field:NotBlank val city: String,
            @field:NotBlank val zipCode: String,
            @field:NotBlank val phoneNumber: String,
            @field:NotBlank val operatingHours: String,
    )

    data class AddRestaurantMenuItemRequest(
            @field:NotBlank val name: String,
            val description: String?,
            @field:NotNull @field:Positive val price: Double,
            @field:NotBlank val category: String,
    )

    data class RemoveRestaurantMenuItemRequest(
    // No request body needed for delete
    )
}
