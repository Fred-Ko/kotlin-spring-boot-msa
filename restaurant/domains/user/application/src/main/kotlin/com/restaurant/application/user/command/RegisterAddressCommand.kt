package com.restaurant.application.user.command

data class RegisterAddressCommand(
    val userId: Long,
    val street: String,
    val detail: String,
    val zipCode: String,
    val isDefault: Boolean = false,
)
