package com.ddd.restaurant.application.command.result

import java.util.UUID

sealed class DeleteRestaurantResult {
    data class Success(val restaurantId: UUID) : DeleteRestaurantResult()
    sealed class Failure : DeleteRestaurantResult() {
        data class RestaurantNotFound(val restaurantId: UUID) : Failure()
        data class DeleteFailed(val message: String) : Failure()
        data class ValidationError(val message: String) : Failure()
    }
} 