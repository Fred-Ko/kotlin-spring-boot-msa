package com.restaurant.application.user.command

data class DeleteAddressCommand(
    val userId: String,
    val addressId: String,
)
