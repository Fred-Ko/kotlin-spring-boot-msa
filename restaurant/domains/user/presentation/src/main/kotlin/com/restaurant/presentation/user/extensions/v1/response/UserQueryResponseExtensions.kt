package com.restaurant.presentation.user.extensions.v1.response

import com.restaurant.application.user.dto.UserProfileDto
import com.restaurant.presentation.user.v1.query.dto.response.AddressResponseV1
import com.restaurant.presentation.user.v1.query.dto.response.UserProfileResponseV1

// UserProfileDto -> UserProfileResponseV1 변환
fun UserProfileDto.toResponse(): UserProfileResponseV1 =
    UserProfileResponseV1(
        id = this.id,
        email = this.email,
        name = this.name,
        addresses = this.addresses.map { it.toResponse() },
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )

// UserProfileDto.AddressDto -> AddressResponseV1 변환
fun UserProfileDto.AddressDto.toResponse(): AddressResponseV1 =
    AddressResponseV1(
        id = this.id,
        street = this.street,
        detail = this.detail,
        zipCode = this.zipCode,
        isDefault = this.isDefault,
    )

// List<UserProfileDto.AddressDto> -> List<AddressResponseV1> 변환
fun List<UserProfileDto.AddressDto>.toResponse(): List<AddressResponseV1> = map { it.toResponse() }
