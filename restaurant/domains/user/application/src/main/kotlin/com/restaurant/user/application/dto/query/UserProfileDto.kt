package com.restaurant.user.application.dto.query

import java.time.Instant

data class UserProfileDto(
    val id: String,
    val email: String,
    val name: String,
    val username: String,
    val phoneNumber: String?,
    val userType: String,
    val addresses: List<AddressDto>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val status: String,
    val version: Long,
) {
    data class AddressDto(
        val id: String,
        val street: String,
        val detail: String,
        val zipCode: String,
        val isDefault: Boolean,
    )
}
