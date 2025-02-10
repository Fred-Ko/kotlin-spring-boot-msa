package com.ddd.restaurant.domain.model.vo

import com.ddd.restaurant.domain.exception.InvalidMenuItemException
import java.math.BigDecimal

data class MenuItem(val name: String, val price: BigDecimal, val quantity: Int) {
    init {
        if (name.isBlank()) {
            throw InvalidMenuItemException("Menu item name cannot be blank")
        }
        if (price < BigDecimal.ZERO) {
            throw InvalidMenuItemException("Price cannot be negative")
        }
        if (quantity <= 0) {
            throw InvalidMenuItemException("Quantity must be greater than 0")
        }
    }

    companion object {
        fun of(name: String, price: BigDecimal, quantity: Int): MenuItem {
            return MenuItem(name, price, quantity)
        }
    }
}
