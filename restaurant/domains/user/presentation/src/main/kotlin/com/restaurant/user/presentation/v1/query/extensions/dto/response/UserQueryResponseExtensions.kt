package com.restaurant.user.presentation.v1.query.extensions.dto.response

import com.restaurant.user.application.query.dto.UserProfileDto
import com.restaurant.user.presentation.v1.query.dto.response.AddressResponseV1
import com.restaurant.user.presentation.v1.query.dto.response.UserProfileResponseV1

fun UserProfileDto.toResponseV1(): UserProfileResponseV1 =
    UserProfileResponseV1(
        id = this.id,
        email = this.email,
        name = this.name,
        username = this.username,
        phoneNumber = this.phoneNumber,
        userType = this.userType,
        addresses = this.addresses.map { it.toResponseV1() },
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        status = this.status,
        version = this.version,
    )

fun UserProfileDto.AddressDto.toResponseV1(): AddressResponseV1 =
    AddressResponseV1(
        id = this.id,
        name = this.name,
        streetAddress = this.streetAddress,
        detailAddress = this.detailAddress,
        city = this.city,
        state = this.state,
        country = this.country,
        zipCode = this.zipCode,
        isDefault = this.isDefault,
    )
