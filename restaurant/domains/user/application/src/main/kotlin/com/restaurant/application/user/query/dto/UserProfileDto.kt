package com.restaurant.application.user.query.dto

import java.time.LocalDateTime

data class UserProfileDto(
    val id: Long,
    val email: String,
    val name: String,
    val addresses: List<AddressDto> = emptyList(),
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    data class AddressDto(
        val id: Long?,
        val street: String,
        val detail: String,
        val zipCode: String,
        val isDefault: Boolean,
    )
}
