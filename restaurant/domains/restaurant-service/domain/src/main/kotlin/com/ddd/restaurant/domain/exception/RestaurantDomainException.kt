package com.ddd.restaurant.domain.exception

import com.ddd.support.exception.DomainException

sealed class RestaurantDomainException(message: String) : DomainException(message) {

    class InvalidMenuException(message: String) : RestaurantDomainException(message)

    class MenuNotFoundException(message: String) : RestaurantDomainException(message)

    class InvalidOperatingHoursException(message: String) : RestaurantDomainException(message)
}
