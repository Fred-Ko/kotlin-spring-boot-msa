package com.restaurant.infrastructure.user.extensions

import com.restaurant.domain.user.entity.Address
import com.restaurant.infrastructure.user.entity.AddressEntity

// AddressEntity -> Address 변환
fun AddressEntity.toDomain(): Address {
    val id = this.id ?: throw IllegalStateException("영속화된 AddressEntity의 ID는 null일 수 없습니다")

    return Address.reconstitute(
        id = id,
        street = street,
        detail = detail,
        zipCode = zipCode,
        isDefault = isDefault,
    )
}

// Address -> AddressEntity 변환 (UserEntity 참조 없이)
fun Address.toEntity(): AddressEntity =
    AddressEntity(
        id = id,
        street = street,
        detail = detail,
        zipCode = zipCode,
        isDefault = isDefault,
    )
