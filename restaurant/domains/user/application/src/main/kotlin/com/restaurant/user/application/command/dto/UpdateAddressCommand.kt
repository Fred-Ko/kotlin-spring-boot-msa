package com.restaurant.user.application.command.dto

data class UpdateAddressCommand(
    val userId: String,
    val addressId: String,
    val name: String,
    val street: String,
    val detail: String,
    val city: String,
    val state: String,
    val country: String,
    val zipCode: String,
    val isDefault: Boolean,
)
