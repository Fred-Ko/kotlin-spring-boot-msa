package com.restaurant.user.application.query.dto

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
        val street: String?, // Nullable로 변경
        val detail: String?, // Nullable로 변경
        val zipCode: String?, // Nullable로 변경
        val isDefault: Boolean,
    )
}
