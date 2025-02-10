package com.ddd.restaurant.application.dto.query

import com.ddd.restaurant.domain.model.vo.MenuItem
import com.ddd.restaurant.domain.model.vo.RestaurantAddress
import com.ddd.restaurant.domain.model.vo.RestaurantOperationHours
import com.ddd.restaurant.domain.model.vo.RestaurantStatus
import java.util.UUID

data class RestaurantDetailResult(
    val id: UUID,
    val name: String,
    val address: RestaurantAddress,
    val menuItems: List<MenuItem>,
    val status: RestaurantStatus,
    val operationHours: RestaurantOperationHours
) 