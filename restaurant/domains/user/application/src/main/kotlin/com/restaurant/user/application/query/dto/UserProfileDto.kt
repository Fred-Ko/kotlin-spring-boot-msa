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
        val name: String,
        val streetAddress: String,
        val detailAddress: String?,
        val city: String,
        val state: String,
        val country: String,
        val zipCode: String,
        val isDefault: Boolean,
    )
}
