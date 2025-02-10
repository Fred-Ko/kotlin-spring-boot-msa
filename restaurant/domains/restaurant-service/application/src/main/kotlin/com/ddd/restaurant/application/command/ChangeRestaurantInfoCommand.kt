package com.ddd.restaurant.application.command

import com.ddd.restaurant.application.dto.command.ChangeRestaurantInfoCommandDto
import com.ddd.restaurant.application.dto.result.ChangeRestaurantInfoResult

interface ChangeRestaurantInfoCommand {
    fun changeRestaurantInfo(command: ChangeRestaurantInfoCommandDto): ChangeRestaurantInfoResult
}
