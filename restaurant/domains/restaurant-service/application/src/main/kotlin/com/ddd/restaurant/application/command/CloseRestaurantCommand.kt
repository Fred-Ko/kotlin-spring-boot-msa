package com.ddd.restaurant.application.command

import com.ddd.restaurant.application.dto.command.CloseRestaurantCommandDto
import com.ddd.restaurant.application.dto.result.CloseRestaurantResult

interface CloseRestaurantCommand {
    fun closeRestaurant(command: CloseRestaurantCommandDto): CloseRestaurantResult
}
