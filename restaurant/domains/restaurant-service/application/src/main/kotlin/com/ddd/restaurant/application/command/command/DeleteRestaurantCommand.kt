package com.ddd.restaurant.application.command.command

import java.util.UUID

data class DeleteRestaurantCommand(
    val restaurantId: UUID
) 