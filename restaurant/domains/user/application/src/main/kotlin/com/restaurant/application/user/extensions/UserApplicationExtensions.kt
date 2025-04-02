package com.restaurant.application.user.extensions

import com.restaurant.application.user.query.dto.UserProfileDto
import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.entity.Address

/**
 * Domain 객체를 Application DTO로 변환하는 확장 함수들
 */

fun User.toUserProfileDto(): UserProfileDto =
    UserProfileDto(
        id = this.id?.value ?: throw IllegalStateException("User ID는 null일 수 없습니다"),
        email = this.email.value,
        name = this.name.value,
        addresses = this.addresses.map { it.toAddressDto() },
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )

fun Address.toAddressDto(): UserProfileDto.AddressDto =
    UserProfileDto.AddressDto(
        id = this.id ?: throw IllegalStateException("Address ID는 null일 수 없습니다"),
        street = this.street,
        detail = this.detail,
        zipCode = this.zipCode,
        isDefault = this.isDefault,
    )
