package com.restaurant.user.infrastructure.persistence.extensions

import com.restaurant.user.domain.entity.Address
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.infrastructure.persistence.entity.AddressEntity
import com.restaurant.user.infrastructure.persistence.entity.UserEntity
import java.time.Instant

/**
 * Extension functions for mapping between Address domain entity and AddressEntity.
 * Rule 24, 25, 60
 */

// AddressEntity -> Address Domain
fun AddressEntity.toDomain(): Address {
    return Address.reconstitute(
        addressId = AddressId.of(this.addressId),
        street = this.street,
        detail = this.detail,
        zipCode = this.zipCode,
        isDefault = this.isDefault,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        version = this.version
    )
}

// Address Domain -> AddressEntity
fun Address.toEntity(userEntity: UserEntity? = null): AddressEntity {
    // Note: Passing userEntity here might be less common than setting it from the UserEntity side.
    // If userEntity is null, it assumes the relationship is managed elsewhere.
    return AddressEntity(
        addressId = this.addressId.value,
        street = this.street,
        detail = this.detail,
        zipCode = this.zipCode,
        isDefault = this.isDefault,
        version = this.version,
        user = userEntity,
        // Let JPA handle the Long id
        // id = null
    )
}
