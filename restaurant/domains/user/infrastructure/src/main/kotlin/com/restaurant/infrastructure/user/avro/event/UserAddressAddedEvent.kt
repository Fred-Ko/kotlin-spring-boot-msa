package com.restaurant.infrastructure.user.avro.event

/**
 * 임시 UserAddressAddedEvent 클래스 (Avro 생성 전 컴파일 오류 방지용)
 * 빌드 과정에서 Avro 스키마로부터 자동 생성될 예정
 */
data class UserAddressAddedEvent(
    val userId: String,
    val addressId: String,
    val nickname: String,
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String,
    val isDefault: Boolean,
    val createdAt: Long,
)
