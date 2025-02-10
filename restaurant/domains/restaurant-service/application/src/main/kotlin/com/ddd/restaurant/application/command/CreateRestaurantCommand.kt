package com.ddd.restaurant.application.command

import com.ddd.restaurant.application.dto.command.CreateRestaurantCommandDto
import com.ddd.restaurant.application.dto.result.CreateRestaurantResult

interface CreateRestaurantCommand {
    fun createRestaurant(command: CreateRestaurantCommandDto): CreateRestaurantResult
}
