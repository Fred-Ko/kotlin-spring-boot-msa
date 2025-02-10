package com.ddd.restaurant.application.dto.command

import java.util.UUID

data class RemoveRestaurantMenuItemCommandDto(val restaurantId: UUID, val menuItemId: String)
