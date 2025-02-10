package com.ddd.restaurant.domain.model.vo

import com.ddd.restaurant.domain.exception.InvalidRestaurantOperationHoursException
import java.time.LocalTime

data class RestaurantOperationHours(val startTime: LocalTime, val endTime: LocalTime) {
    init {
        if (endTime <= startTime) {
            throw InvalidRestaurantOperationHoursException("End time must be after start time")
        }
    }

    companion object {
        fun of(startTime: LocalTime, endTime: LocalTime): RestaurantOperationHours {
            return RestaurantOperationHours(startTime, endTime)
        }
    }
}
