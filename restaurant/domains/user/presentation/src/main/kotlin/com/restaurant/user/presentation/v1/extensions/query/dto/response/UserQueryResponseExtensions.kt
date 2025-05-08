package com.restaurant.user.presentation.v1.extensions.query.dto.response

import com.restaurant.user.application.dto.query.UserProfileDto
import com.restaurant.user.presentation.v1.dto.response.AddressResponseV1
import com.restaurant.user.presentation.v1.dto.response.UserProfileResponseV1
import org.springframework.hateoas.Link

fun UserProfileDto.toResponseV1(): UserProfileResponseV1 {
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
        addresses = this.addresses.map { it.toResponseV1() },
    ).apply {
        add(Link.of("/api/v1/users/$userId/profile", "self"))
        add(Link.of("/api/v1/users/$userId/profile", "update-profile"))
        add(Link.of("/api/v1/users/$userId/password", "change-password"))
        add(Link.of("/api/v1/users/$userId", "delete-user"))
    }
}

fun UserProfileDto.AddressDto.toResponseV1(): AddressResponseV1 =
    AddressResponseV1(
        id = this.id,
        street = this.street,
        detail = this.detail,
        zipCode = this.zipCode,
        isDefault = this.isDefault,
    )

fun List<UserProfileDto.AddressDto>.toResponseV1(): List<AddressResponseV1> = map { it.toResponseV1() }
