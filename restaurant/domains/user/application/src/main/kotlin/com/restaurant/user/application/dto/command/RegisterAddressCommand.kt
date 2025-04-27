package com.restaurant.user.application.dto.command

data class RegisterAddressCommand(
    val userId: String,
    val street: String,
    val detail: String,
    val zipCode: String,
    val isDefault: Boolean = false,
)
