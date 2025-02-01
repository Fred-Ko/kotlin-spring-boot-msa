package com.ddd.restaurant.application.command.command

import com.ddd.restaurant.domain.model.vo.Location
import com.ddd.restaurant.domain.model.vo.OperatingHours
import com.ddd.restaurant.domain.model.vo.RestaurantName

data class CreateRestaurantCommand(
        val name: RestaurantName,
        val operatingHours: OperatingHours,
        val location: Location,
        val menus: List<MenuCommand>? = null
) {
    data class MenuCommand(val name: String, val price: Double)
}
