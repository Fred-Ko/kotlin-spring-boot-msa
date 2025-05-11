package com.restaurant.user.application.dto.command

data class RegisterAddressCommand(
    val userId: String,
    val name: String,
    val street: String,
    val detail: String,
    val city: String,
    val state: String,
    val country: String,
    val zipCode: String,
    val isDefault: Boolean = false,
)
