package com.restaurant.presentation.user.v1.dto.response

data class AddressResponseV1(
    val id: Long,
    val street: String,
    val detail: String,
    val zipCode: String,
    val isDefault: Boolean,
)
