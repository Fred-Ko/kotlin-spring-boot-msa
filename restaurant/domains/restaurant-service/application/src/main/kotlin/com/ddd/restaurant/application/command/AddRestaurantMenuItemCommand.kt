package com.ddd.restaurant.application.command

import com.ddd.restaurant.application.dto.command.AddRestaurantMenuItemCommandDto
import com.ddd.restaurant.application.dto.result.AddRestaurantMenuItemResult

interface AddRestaurantMenuItemCommand {
    fun addRestaurantMenuItem(command: AddRestaurantMenuItemCommandDto): AddRestaurantMenuItemResult
}
