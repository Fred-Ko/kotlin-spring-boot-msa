package com.ddd.restaurant.application.query.result

import com.ddd.restaurant.application.query.dto.RestaurantDto
import org.springframework.data.domain.Page

sealed class GetRestaurantsResult {
    data class Success(val restaurantsPage: Page<RestaurantDto>) : GetRestaurantsResult()
    sealed class Failure : GetRestaurantsResult() {
        data class ValidationError(val message: String) : Failure()
    }
}
