package com.restaurant.application.user.dto

import java.time.LocalDateTime

data class UserProfileDto(
    val id: String,
    val email: String,
    val name: String,
    val addresses: List<AddressDto> = emptyList(),
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    data class AddressDto(
        val id: String,
        val street: String,
        val detail: String,
        val zipCode: String,
        val isDefault: Boolean,
    )
}
