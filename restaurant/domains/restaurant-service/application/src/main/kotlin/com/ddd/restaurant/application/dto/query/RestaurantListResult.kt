package com.ddd.restaurant.application.dto.query

import com.ddd.restaurant.domain.model.vo.RestaurantAddress
import com.ddd.restaurant.domain.model.vo.RestaurantStatus
import java.util.UUID

data class RestaurantListResult(
    val restaurants: List<RestaurantInfo>
) {
    data class RestaurantInfo(
        val id: UUID,
        val name: String,
        val address: RestaurantAddress,
        val status: RestaurantStatus
    )
} 