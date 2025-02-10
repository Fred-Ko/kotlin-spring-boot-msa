package com.ddd.restaurant.application.command

import com.ddd.restaurant.application.dto.command.RemoveRestaurantMenuItemCommandDto
import com.ddd.restaurant.application.dto.result.RemoveRestaurantMenuItemResult

interface RemoveRestaurantMenuItemCommand {
    fun removeRestaurantMenuItem(
            command: RemoveRestaurantMenuItemCommandDto
    ): RemoveRestaurantMenuItemResult
}
