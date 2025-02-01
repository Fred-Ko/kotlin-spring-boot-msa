package com.ddd.restaurant.application.command.command

import com.ddd.restaurant.domain.model.vo.Location
import com.ddd.restaurant.domain.model.vo.OperatingHours
import com.ddd.restaurant.domain.model.vo.RestaurantName
import java.util.UUID

data class UpdateRestaurantCommand(
    val restaurantId: UUID,
    val name: RestaurantName? = null,
    val operatingHours: OperatingHours? = null,
    val location: Location? = null,
    val menus: List<MenuCommand>? = null
) {
    data class MenuCommand(
        val id: UUID? = null,
        val name: String? = null,
        val price: Double? = null
    )
} 