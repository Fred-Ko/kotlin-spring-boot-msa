package com.restaurant.user.application.dto.command

data class UpdateAddressCommand(
    val userId: String,
    val addressId: String,
    val street: String,
    val detail: String,
    val zipCode: String,
    val isDefault: Boolean,
)
