package com.restaurant.user.application.dto.query

import java.time.Instant

// Application 레이어의 Query Result DTO (Rule App-Struct)
data class UserProfileDto(
    // 사용자 ID
    val id: String,
    // 이메일
    val email: String,
    // 이름
    val name: String,
    // 닉네임
    val username: String,
    // 전화번호
    val phoneNumber: String?,
    // 사용자 유형
    val userType: String,
    // 주소 목록
    val addresses: List<AddressDto>,
    // 생성일
    val createdAt: Instant,
    // 수정일
    val updatedAt: Instant,
    // 상태
    val status: String,
    // 버전
    val version: Long,
) {
    data class AddressDto(
        // UUID String
        val id: String,
        val street: String,
        val detail: String,
        // 우편번호
        val zipCode: String,
        val isDefault: Boolean,
    )
}
