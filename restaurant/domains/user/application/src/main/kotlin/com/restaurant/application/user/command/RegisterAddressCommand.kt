package com.restaurant.application.user.command

data class RegisterAddressCommand(
    val userId: String,
    val street: String,
    val detail: String,
    val zipCode: String,
    val isDefault: Boolean = false,
)
