package com.ddd.restaurant.application.query.result

import com.ddd.restaurant.application.query.dto.RestaurantDto

sealed class GetRestaurantResult {
    data class Success(val restaurant: RestaurantDto) : GetRestaurantResult()
    sealed class Failure : GetRestaurantResult() {
        data class RestaurantNotFound(val restaurantId: String) : Failure()
        data class ValidationError(val message: String) : Failure()
    }
}
