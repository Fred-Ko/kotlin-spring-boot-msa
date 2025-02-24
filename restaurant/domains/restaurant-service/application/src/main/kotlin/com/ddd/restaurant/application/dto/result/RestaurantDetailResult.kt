package com.ddd.restaurant.application.dto.result

import java.math.BigDecimal
import java.time.LocalTime
import java.util.UUID

data class RestaurantDetailResult(
        val id: UUID,
        val name: String,
        val address: Address,
        val menuItems: List<MenuItem>,
        val status: RestaurantStatus,
        val operationHours: OperationHours
) {
    data class Address(val street: String, val city: String, val zipCode: String)

    data class MenuItem(val name: String, val price: BigDecimal, val quantity: Int)

    data class OperationHours(val startTime: LocalTime, val endTime: LocalTime)

    enum class RestaurantStatus {
        OPEN,
        CLOSED
    }
}
