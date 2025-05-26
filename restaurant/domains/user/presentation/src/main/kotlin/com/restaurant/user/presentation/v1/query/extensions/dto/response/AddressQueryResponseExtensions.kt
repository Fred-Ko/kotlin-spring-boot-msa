package com.restaurant.user.presentation.v1.query.extensions.dto.response

import com.restaurant.user.application.query.dto.AddressDto
import com.restaurant.user.presentation.v1.query.dto.response.AddressDetailResponseV1
import com.restaurant.user.presentation.v1.query.dto.response.AddressResponseV1

fun AddressDto.toDetailResponseV1(): AddressDetailResponseV1 =
    AddressDetailResponseV1(
        id = this.id,
        name = this.name,
        streetAddress = this.streetAddress,
        detailAddress = this.detailAddress,
        city = this.city,
        state = this.state,
        country = this.country,
        zipCode = this.zipCode,
        isDefault = this.isDefault,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        version = this.version,
    )

fun AddressDto.toResponseV1(): AddressResponseV1 =
    AddressResponseV1(
        id = this.id,
        name = this.name,
        street = this.streetAddress,
        detail = this.detailAddress ?: "",
        city = this.city,
        state = this.state,
        country = this.country,
        zipCode = this.zipCode,
        isDefault = this.isDefault,
    )
