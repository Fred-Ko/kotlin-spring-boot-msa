package com.ddd.restaurant.application.command

import com.ddd.restaurant.application.dto.command.ChangeRestaurantMenuCommandDto
import com.ddd.restaurant.application.dto.result.ChangeRestaurantMenuResult

interface ChangeRestaurantMenuCommand {
    fun changeRestaurantMenu(command: ChangeRestaurantMenuCommandDto): ChangeRestaurantMenuResult
}
