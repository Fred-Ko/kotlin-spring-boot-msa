package com.ddd.restaurant.application.dto.command

import java.time.LocalTime
import java.util.UUID

data class ChangeRestaurantOperationHoursCommandDto(
        val restaurantId: UUID,
        val newOperationHours: RestaurantOperationHoursDto
) {
        data class RestaurantOperationHoursDto(val startTime: LocalTime, val endTime: LocalTime)
}
