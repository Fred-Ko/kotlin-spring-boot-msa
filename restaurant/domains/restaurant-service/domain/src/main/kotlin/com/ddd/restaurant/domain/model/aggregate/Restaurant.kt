package com.ddd.restaurant.domain.model.aggregate

import com.ddd.restaurant.domain.exception.RestaurantDomainException
import com.ddd.restaurant.domain.model.entity.Menu
import com.ddd.restaurant.domain.model.vo.*
import java.time.LocalDateTime
import java.util.UUID
import org.springframework.data.domain.AbstractAggregateRoot

data class Restaurant(
        val id: UUID,
        var name: RestaurantName,
        var menus: MutableList<Menu> = mutableListOf(),
        var operatingHours: OperatingHours,
        var status: RestaurantStatus,
        var location: Location,
        val createdAt: LocalDateTime,
        var updatedAt: LocalDateTime,
        private var version: Long = 0
) : AbstractAggregateRoot<Restaurant>() {

    companion object {
        fun create(
                name: RestaurantName,
                operatingHours: OperatingHours,
                location: Location
        ): Restaurant {
            return Restaurant(
                    id = UUID.randomUUID(),
                    name = name,
                    operatingHours = operatingHours,
                    status = RestaurantStatus.CLOSED,
                    location = location,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
            )
        }
    }

    fun open() {
        validateOperatingHours()
        require(menus.isNotEmpty()) {
            throw RestaurantDomainException.MenuNotFoundException("Menu cannot be empty")
        }
        status = RestaurantStatus.OPEN
        updatedAt = LocalDateTime.now()
    }

    fun updateName(newName: RestaurantName) {
        this.name = newName
        this.updatedAt = LocalDateTime.now()
    }

    fun updateMenu(newMenu: Menu) {
        val existingMenuIndex = menus.indexOfFirst { it.id == newMenu.id }
        require(existingMenuIndex != -1) {
            throw RestaurantDomainException.MenuNotFoundException("Menu not found")
        }
        menus[existingMenuIndex] = newMenu
        updatedAt = LocalDateTime.now()
    }

    fun updateOperatingHours(newHours: OperatingHours) {
        validateOperatingHours()
        this.operatingHours = newHours
        this.updatedAt = LocalDateTime.now()
    }

    fun updateLocation(newLocation: Location) {
        this.location = newLocation
        this.updatedAt = LocalDateTime.now()
    }

    fun close() {
        this.status = RestaurantStatus.CLOSED
        this.updatedAt = LocalDateTime.now()
    }

    fun reopen() {
        validateOperatingHours()
        this.status = RestaurantStatus.OPEN
        this.updatedAt = LocalDateTime.now()
    }

    private fun validateOperatingHours() {
        require(operatingHours.isValid()) { "Invalid operating hours" }
    }
}
