package com.ddd.restaurant.application.command

import com.ddd.restaurant.application.dto.command.OpenRestaurantCommandDto
import com.ddd.restaurant.application.dto.result.OpenRestaurantResult

interface OpenRestaurantCommand {
    fun openRestaurant(command: OpenRestaurantCommandDto): OpenRestaurantResult
}
