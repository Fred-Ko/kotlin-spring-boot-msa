package com.restaurant.user.application.dto.command

data class DeleteAddressCommand(
    val userId: String,
    val addressId: String,
)
