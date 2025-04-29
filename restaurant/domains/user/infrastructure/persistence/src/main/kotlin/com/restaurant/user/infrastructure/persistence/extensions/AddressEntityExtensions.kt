package com.restaurant.user.infrastructure.persistence.extensions

import com.restaurant.user.domain.entity.Address // Import Address domain entity
import com.restaurant.user.domain.vo.AddressId // Import AddressId VO
import com.restaurant.user.infrastructure.persistence.entity.AddressEntity
import com.restaurant.user.infrastructure.persistence.entity.UserEntity // Added import for user property in toEntity

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
        version = this.version // Add version mapping
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
        version = this.version, // Add version mapping
        user = userEntity // Set user relationship if provided
        // id = null // Let JPA handle the Long id
    )
}
