package com.restaurant.user.application.query.dto

data class GetAddressByIdQuery(
    val userId: String,
    val addressId: String,
)
