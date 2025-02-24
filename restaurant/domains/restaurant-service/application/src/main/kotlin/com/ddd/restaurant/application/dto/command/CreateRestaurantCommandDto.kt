package com.ddd.restaurant.application.dto.command

import java.math.BigDecimal
import java.time.LocalTime

data class CreateRestaurantCommandDto(
        val name: String,
        val address: RestaurantAddressDto,
        val menuItems: List<MenuItemDto>,
        val operationHours: RestaurantOperationHoursDto
) {
        data class RestaurantAddressDto(val street: String, val city: String, val zipCode: String)
        data class MenuItemDto(val name: String, val price: BigDecimal, val quantity: Int)
        data class RestaurantOperationHoursDto(val startTime: LocalTime, val endTime: LocalTime)
}
