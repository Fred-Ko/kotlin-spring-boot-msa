package com.ddd.restaurant.application.command.result

import java.util.UUID

sealed class CreateRestaurantResult {
    data class Success(val restaurantId: UUID) : CreateRestaurantResult()
    sealed class Failure : CreateRestaurantResult() {
        data class InvalidInput(val message: String) : Failure()
        data class RestaurantCreationFailed(val message: String) : Failure()
        data class ValidationError(val message: String) : Failure()
    }
}
