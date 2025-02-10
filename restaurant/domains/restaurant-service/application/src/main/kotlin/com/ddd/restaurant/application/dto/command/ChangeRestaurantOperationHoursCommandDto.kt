package com.ddd.restaurant.application.dto.command

import com.ddd.restaurant.domain.model.vo.RestaurantOperationHours
import java.util.UUID

data class ChangeRestaurantOperationHoursCommandDto(
        val restaurantId: UUID,
        val newOperationHours: RestaurantOperationHours
)
