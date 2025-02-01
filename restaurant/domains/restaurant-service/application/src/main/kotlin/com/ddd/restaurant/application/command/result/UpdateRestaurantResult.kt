package com.ddd.restaurant.application.command.result

import java.util.UUID

sealed class UpdateRestaurantResult {
    data class Success(val restaurantId: UUID) : UpdateRestaurantResult()
    sealed class Failure : UpdateRestaurantResult() {
        data class RestaurantNotFound(val restaurantId: UUID) : Failure()
        data class UpdateFailed(val message: String) : Failure()
        data class ValidationError(val message: String) : Failure()
    }
} 