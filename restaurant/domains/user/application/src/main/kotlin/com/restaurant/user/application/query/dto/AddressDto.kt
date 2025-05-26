package com.restaurant.user.application.query.dto

import java.time.Instant

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
    val createdAt: Instant,
    val updatedAt: Instant,
    val version: Long,
)
