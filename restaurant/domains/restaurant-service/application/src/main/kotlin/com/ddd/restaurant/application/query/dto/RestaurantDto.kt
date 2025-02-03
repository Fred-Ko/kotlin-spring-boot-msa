package com.ddd.restaurant.application.query.dto

import com.ddd.restaurant.domain.model.aggregate.Restaurant
import java.time.LocalTime
import java.util.UUID

data class RestaurantDto(
        val id: UUID,
        val name: String,
        val status: String,
        val operatingHours: OperatingHoursDto,
        val location: LocationDto,
        val menus: List<MenuDto>? = null
) {
    data class OperatingHoursDto(val openTime: LocalTime, val closeTime: LocalTime)

    data class LocationDto(val latitude: Double, val longitude: Double)

    data class MenuDto(val name: String, val price: Double)

    companion object {
        fun from(restaurant: Restaurant) =
                RestaurantDto(
                        id = restaurant.id,
                        name = restaurant.name.value,
                        status = restaurant.status.toString(),
                        operatingHours =
                                OperatingHoursDto(
                                        openTime = restaurant.operatingHours.startTime,
                                        closeTime = restaurant.operatingHours.endTime
                                ),
                        location =
                                LocationDto(
                                        latitude = restaurant.location.latitude,
                                        longitude = restaurant.location.longitude
                                ),
                        menus = restaurant.menus?.map { MenuDto(name = it.name, price = it.price) }
                )
    }
}
