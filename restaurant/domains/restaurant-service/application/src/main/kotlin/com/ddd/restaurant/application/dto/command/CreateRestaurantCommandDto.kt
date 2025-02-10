package com.ddd.restaurant.application.dto.command

import com.ddd.restaurant.domain.model.vo.MenuItem
import com.ddd.restaurant.domain.model.vo.RestaurantAddress
import com.ddd.restaurant.domain.model.vo.RestaurantOperationHours

data class CreateRestaurantCommandDto(
        val name: String,
        val address: RestaurantAddress,
        val menuItems: List<MenuItem>,
        val operationHours: RestaurantOperationHours
)
