package com.ddd.restaurant.application.command.command

data class CreateRestaurantCommand(
        val name: String,
        val operatingHours: OperatingHoursDto,
        val location: LocationDto,
        val menus: List<MenuCommand>? = null
) {
    data class OperatingHoursDto(val openTime: String, val closeTime: String)

    data class LocationDto(val address: String, val latitude: Double, val longitude: Double)

    data class MenuCommand(val name: String, val price: Double)
}
