package com.ddd.restaurant.presentation.api.v1.query.dto.response

import com.ddd.restaurant.application.query.dto.RestaurantDto
import java.util.UUID

data class RestaurantResponse(
        val id: UUID,
        val name: String,
        val status: String,
        val operatingHours: OperatingHoursDto,
        val location: LocationDto,
        val menus: List<MenuDto>? = null
) {
  data class OperatingHoursDto(val openTime: String, val closeTime: String)

  data class LocationDto(val latitude: Double, val longitude: Double)

  data class MenuDto(val name: String, val price: Double)

  companion object {
    fun from(dto: RestaurantDto): RestaurantResponse {
      return RestaurantResponse(
              id = dto.id,
              name = dto.name,
              status = dto.status,
              operatingHours =
                      OperatingHoursDto(
                              openTime = dto.operatingHours.openTime.toString(),
                              closeTime = dto.operatingHours.closeTime.toString()
                      ),
              location =
                      LocationDto(
                              latitude = dto.location.latitude,
                              longitude = dto.location.longitude
                      ),
              menus = dto.menus?.map { MenuDto(name = it.name, price = it.price) }
      )
    }
  }
}
