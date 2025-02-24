package com.ddd.restaurant.application.dto.command

import java.math.BigDecimal
import java.util.UUID

data class AddRestaurantMenuItemCommandDto(val restaurantId: UUID, val menuItem: MenuItemDto) {
    data class MenuItemDto(val name: String, val price: BigDecimal, val quantity: Int)
}
