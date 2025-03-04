package com.ddd.restaurant.domain.model.aggregate

import com.ddd.restaurant.domain.exception.MenuItemNotFoundException
import com.ddd.restaurant.domain.model.vo.*
import java.time.LocalDateTime
import java.util.UUID

@ConsistentCopyVisibility
data class Restaurant
private constructor(
        val id: UUID,
        val name: String,
        val address: RestaurantAddress,
        val menuItems: List<MenuItem>,
        val status: RestaurantStatus,
        val operationHours: RestaurantOperationHours,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
        val version: Long = 0
) {

    companion object {
        fun create(
                id: UUID = UUID.randomUUID(),
                name: String,
                address: RestaurantAddress,
                menuItems: List<MenuItem>,
                operationHours: RestaurantOperationHours,
                status: RestaurantStatus = RestaurantStatus.OPEN,
                createdAt: LocalDateTime = LocalDateTime.now(),
                updatedAt: LocalDateTime = LocalDateTime.now(),
                version: Long = 0
        ): Restaurant {
            return Restaurant(
                    id = id,
                    name = name,
                    address = address,
                    menuItems = menuItems,
                    status = status,
                    operationHours = operationHours,
                    createdAt = createdAt,
                    updatedAt = updatedAt,
                    version = version
            )
        }
    }

    fun changeMenu(newMenuItems: List<MenuItem>): Restaurant {
        return this.copy(menuItems = newMenuItems, updatedAt = LocalDateTime.now())
    }

    fun addMenuItem(menuItem: MenuItem): Restaurant {
        val newMenuItems = menuItems.toMutableList()
        newMenuItems.add(menuItem)
        return this.copy(menuItems = newMenuItems, updatedAt = LocalDateTime.now())
    }

    fun removeMenuItem(menuItemId: String): Restaurant {
        val newMenuItems = menuItems.filter { it.name != menuItemId }
        if (newMenuItems.size == menuItems.size) {
            throw MenuItemNotFoundException(menuItemId)
        }
        return this.copy(menuItems = newMenuItems, updatedAt = LocalDateTime.now())
    }

    fun changeRestaurantInfo(newName: String?, newAddress: RestaurantAddress?): Restaurant {
        return this.copy(
                name = newName ?: this.name,
                address = newAddress ?: this.address,
                updatedAt = LocalDateTime.now()
        )
    }

    fun changeOperationHours(newOperationHours: RestaurantOperationHours): Restaurant {
        return this.copy(operationHours = newOperationHours, updatedAt = LocalDateTime.now())
    }

    fun closeRestaurant(): Restaurant {
        return this.copy(status = RestaurantStatus.CLOSED, updatedAt = LocalDateTime.now())
    }

    fun openRestaurant(): Restaurant {
        return this.copy(status = RestaurantStatus.OPEN, updatedAt = LocalDateTime.now())
    }
}
