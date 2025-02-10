package com.ddd.restaurant.application.dto.command

import com.ddd.restaurant.domain.model.vo.RestaurantAddress
import java.util.UUID

data class ChangeRestaurantInfoCommandDto(
        val restaurantId: UUID,
        val newName: String?,
        val newAddress: RestaurantAddress?
)
