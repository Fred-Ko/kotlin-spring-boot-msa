package com.restaurant.presentation.user.v1.extensions

import com.restaurant.application.user.query.dto.UserProfileDto
import com.restaurant.presentation.user.v1.query.dto.response.AddressResponseV1
import com.restaurant.presentation.user.v1.query.dto.response.UserProfileResponseV1

// UserProfileDto를 UserProfileResponseV1으로 변환
fun UserProfileDto.toResponse(): UserProfileResponseV1 =
    UserProfileResponseV1(
        id = id,
        email = email,
        name = name,
        addresses =
            addresses.map { addressDto ->
                AddressResponseV1(
                    id = addressDto.id ?: 0,
                    street = addressDto.street,
                    detail = addressDto.detail,
                    zipCode = addressDto.zipCode,
                    isDefault = addressDto.isDefault,
                )
            },
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

// UserProfileDto.AddressDto를 AddressResponseV1로 변환
fun UserProfileDto.AddressDto.toResponse(): AddressResponseV1 =
    AddressResponseV1(
        id = id ?: 0,
        street = street,
        detail = detail,
        zipCode = zipCode,
        isDefault = isDefault,
    )
