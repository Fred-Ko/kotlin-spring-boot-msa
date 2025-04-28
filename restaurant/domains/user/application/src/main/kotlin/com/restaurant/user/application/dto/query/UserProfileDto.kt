package com.restaurant.user.application.dto.query

import java.time.Instant // Instant 사용 권장

// Application 레이어의 Query Result DTO (Rule App-Struct)
data class UserProfileDto(
    val id: String, // UUID String
    val email: String,
    val name: String,
    val username: String, // 추가됨
    val phoneNumber: String?,
    val userType: String, // Enum 이름 등
    val addresses: List<AddressDto>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val status: String,
    val version: Long,
) {
    data class AddressDto(
        val id: String, // UUID String
        val street: String,
        val detail: String,
        val zipCode: String,
        val isDefault: Boolean,
    )
}
