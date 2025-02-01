package com.ddd.restaurant.domain.model.vo

import java.time.LocalTime

data class OperatingHours(
    val startTime: LocalTime,
    val endTime: LocalTime
) {
    fun isValid(): Boolean {
        return startTime.isBefore(endTime)
    }
} 