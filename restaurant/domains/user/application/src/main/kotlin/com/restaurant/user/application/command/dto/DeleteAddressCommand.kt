package com.restaurant.user.application.command.dto

data class DeleteAddressCommand(
    val userId: String,
    val addressId: String,
)
