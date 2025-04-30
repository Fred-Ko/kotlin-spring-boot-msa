package com.restaurant.user.presentation.v1.extensions.query.dto.response

import com.restaurant.user.application.dto.query.UserProfileDto
import com.restaurant.user.presentation.v1.dto.response.AddressResponseV1
import com.restaurant.user.presentation.v1.dto.response.UserProfileResponseV1
import org.springframework.hateoas.Link

// UserProfileDto -> UserProfileResponseV1 변환
fun UserProfileDto.toResponseV1(): UserProfileResponseV1 {
    // Assuming UserProfileDto has 'id' which is the UUID string
    val userId = this.id

    return UserProfileResponseV1(
        id = this.id,
        username = this.username,
        email = this.email,
        name = this.name,
        phoneNumber = this.phoneNumber,
        userType = this.userType,
        status = this.status,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        version = this.version,
        // Use correct function name
        addresses = this.addresses.map { it.toResponseV1() },
    ).apply {
        // 간단히 문자열 기반으로 링크 추가
        add(Link.of("/api/v1/users/$userId/profile", "self"))
        add(Link.of("/api/v1/users/$userId/profile", "update-profile"))
        add(Link.of("/api/v1/users/$userId/password", "change-password"))
        add(Link.of("/api/v1/users/$userId", "delete-user"))
    }
}

// UserProfileDto.AddressDto -> AddressResponseV1 변환
fun UserProfileDto.AddressDto.toResponseV1(): AddressResponseV1 =
    AddressResponseV1(
        id = this.id,
        street = this.street,
        detail = this.detail,
        zipCode = this.zipCode,
        isDefault = this.isDefault,
    )

// List extension (can be useful)
fun List<UserProfileDto.AddressDto>.toResponseV1(): List<AddressResponseV1> = map { it.toResponseV1() }
