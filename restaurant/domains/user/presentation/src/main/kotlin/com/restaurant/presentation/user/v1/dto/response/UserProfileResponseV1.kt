package com.restaurant.presentation.user.v1.dto.response

import java.time.LocalDateTime

data class UserProfileResponseV1(
    val id: Long,
    val email: String,
    val name: String,
    val addresses: List<AddressResponseV1>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
