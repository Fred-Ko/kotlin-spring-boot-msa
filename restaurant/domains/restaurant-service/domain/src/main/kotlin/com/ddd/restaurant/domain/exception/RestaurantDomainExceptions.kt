package com.ddd.restaurant.domain.exception

sealed class RestaurantDomainException(message: String) : RuntimeException(message)

class RestaurantNotFoundException(restaurantId: String) :
    RestaurantDomainException("Restaurant not found with id: $restaurantId")

class MenuItemNotFoundException(menuItemId: String) :
    RestaurantDomainException("Menu item not found with id: $menuItemId")

class InvalidRestaurantAddressException(message: String) : RestaurantDomainException(message)
class InvalidMenuItemException(message: String) : RestaurantDomainException(message)
class InvalidRestaurantOperationHoursException(message: String) : RestaurantDomainException(message)
