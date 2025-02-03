package com.ddd.restaurant.presentation.api.v1.command.dto.request

import com.ddd.restaurant.application.command.command.CreateRestaurantCommand
import com.ddd.restaurant.application.command.command.UpdateRestaurantCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RestaurantRequest(
        @field:NotBlank(message = "Restaurant name is required")
        @field:Size(max = 100, message = "Restaurant name must not exceed 100 characters")
        val name: String,
        val operatingHours: OperatingHoursDto,
        val location: LocationDto,
        val menus: List<MenuCommand>? = null
) {
        data class OperatingHoursDto(val openTime: String, val closeTime: String)

        data class LocationDto(val address: String, val latitude: Double, val longitude: Double)

        data class MenuCommand(val name: String, val price: Double)

        fun toCreateCommand(): CreateRestaurantCommand {
                return CreateRestaurantCommand(
                        name = name,
                        operatingHours =
                                CreateRestaurantCommand.OperatingHoursDto(
                                        openTime = operatingHours.openTime,
                                        closeTime = operatingHours.closeTime
                                ),
                        location =
                                CreateRestaurantCommand.LocationDto(
                                        address = location.address,
                                        latitude = location.latitude,
                                        longitude = location.longitude
                                ),
                        menus =
                                menus?.map {
                                        CreateRestaurantCommand.MenuCommand(
                                                name = it.name,
                                                price = it.price
                                        )
                                }
                )
        }

        fun toUpdateCommand(restaurantId: String): UpdateRestaurantCommand {
                return UpdateRestaurantCommand(
                        restaurantId = restaurantId,
                        name = name,
                        operatingHours =
                                UpdateRestaurantCommand.OperatingHoursDto(
                                        openTime = operatingHours.openTime,
                                        closeTime = operatingHours.closeTime
                                ),
                        location =
                                UpdateRestaurantCommand.LocationDto(
                                        address = location.address,
                                        latitude = location.latitude,
                                        longitude = location.longitude
                                ),
                        menus =
                                menus?.map {
                                        UpdateRestaurantCommand.MenuCommand(
                                                name = it.name,
                                                price = it.price
                                        )
                                }
                )
        }
}
