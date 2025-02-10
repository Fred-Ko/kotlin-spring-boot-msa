package com.ddd.restaurant.application.dto.command

import com.ddd.restaurant.domain.model.vo.MenuItem
import java.util.UUID

data class AddRestaurantMenuItemCommandDto(val restaurantId: UUID, val menuItem: MenuItem)
