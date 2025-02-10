package com.ddd.restaurant.domain.model.vo

import com.ddd.restaurant.domain.exception.InvalidRestaurantAddressException

data class RestaurantAddress(val street: String, val city: String, val zipCode: String) {
    init {
        if (street.isBlank()) {
            throw InvalidRestaurantAddressException("Street cannot be blank")
        }
        if (city.isBlank()) {
            throw InvalidRestaurantAddressException("City cannot be blank")
        }
        if (zipCode.isBlank()) {
            throw InvalidRestaurantAddressException("ZipCode cannot be blank")
        }
    }

    companion object {
        fun of(street: String, city: String, zipCode: String): RestaurantAddress {
            return RestaurantAddress(street, city, zipCode)
        }
    }
}
