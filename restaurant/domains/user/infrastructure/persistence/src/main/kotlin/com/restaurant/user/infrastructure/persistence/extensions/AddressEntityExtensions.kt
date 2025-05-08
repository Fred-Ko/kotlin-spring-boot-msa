package com.restaurant.user.infrastructure.persistence.extensions

import com.restaurant.user.domain.entity.Address
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.infrastructure.persistence.entity.AddressEntity
import java.time.Instant

/**
 * Extension functions for mapping between Address domain entity and AddressEntity.
 * Rule 24, 25, 60
 */

fun AddressEntity.toDomain(): Address =
    Address.reconstitute(
        addressId = AddressId(this.domainId),
        name = this.name,
        streetAddress = this.streetAddress,
        city = this.city,
        state = this.state,
        country = this.country,
        zipCode = this.zipCode,
        isDefault = this.isDefault,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        version = this.version,
    )

fun Address.toEntity(): AddressEntity =
    AddressEntity(
        domainId = this.addressId.value,
        name = this.name,
        streetAddress = this.streetAddress,
        city = this.city,
        state = this.state,
        country = this.country,
        zipCode = this.zipCode,
        isDefault = this.isDefault,
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
        version = this.version,
    )
