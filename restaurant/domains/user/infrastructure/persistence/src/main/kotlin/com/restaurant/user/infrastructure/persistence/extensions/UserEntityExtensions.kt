package com.restaurant.user.infrastructure.persistence.extensions

import com.restaurant.user.domain.aggregate.User
import com.restaurant.user.domain.aggregate.UserStatus
import com.restaurant.user.domain.aggregate.UserType
import com.restaurant.user.domain.entity.Address
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.infrastructure.persistence.entity.AddressEntity
import com.restaurant.user.infrastructure.persistence.entity.UserEntity
import java.util.stream.Collectors

/**
 * Extension functions for mapping between User domain aggregate and UserEntity.
 * Rule 24, 25, 60
 */

// UserEntity -> User Domain
fun UserEntity.toDomain(): User {
    return User.reconstitute(
        id = UserId.fromUUID(this.userId),
        username = Username.of(this.username),
        password = Password.of(this.passwordHash),
        email = Email.of(this.email),
        name = this.name,
        phoneNumber = this.phoneNumber?.let { PhoneNumber.of(it) },
        userType = this.userType,
        userStatus = this.status,
        addresses = this.addresses.stream().map { it.toDomain() }.collect(Collectors.toList()),
        defaultAddressId = this.addresses.find { it.isDefault }?.let { AddressId.fromUUID(it.addressId) },
        version = this.version
    )
}

// User Domain -> UserEntity
fun User.toEntity(): UserEntity {
    val userEntity = UserEntity(
        userId = this.id.value,
        username = this.username.value,
        passwordHash = this.password.value,
        email = this.email.value,
        name = this.name,
        phoneNumber = this.phoneNumber?.value,
        userType = this.userType,
        status = this.userStatus,
        addresses = mutableListOf(),
        version = this.version,
    )

    val addressEntities = this.addresses.map { address ->
        address.toEntity(userEntity)
    }
    userEntity.addresses.addAll(addressEntities)

    this.defaultAddressId?.let { defId ->
        userEntity.addresses.find { it.addressId == defId.value }?.isDefault = true
    }

    return userEntity
}
