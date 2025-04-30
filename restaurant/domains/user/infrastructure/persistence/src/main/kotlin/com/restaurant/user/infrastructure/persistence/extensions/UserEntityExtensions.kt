package com.restaurant.user.infrastructure.persistence.extensions

import com.restaurant.user.domain.aggregate.User
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.infrastructure.persistence.entity.UserEntity
import com.restaurant.user.domain.vo.Username
import com.restaurant.user.domain.vo.Password
import com.restaurant.user.domain.vo.Email
import com.restaurant.user.domain.vo.Name
import com.restaurant.user.domain.vo.PhoneNumber
import com.restaurant.user.domain.aggregate.UserType
import com.restaurant.user.domain.aggregate.UserStatus
import java.time.Instant



/**
 * Extension functions for mapping between User domain aggregate and UserEntity.
 * Rule 24, 25, 60
 */
// UserEntity -> User Domain
fun UserEntity.toDomain(): User {
    return User(
        id = UserId.fromUUID(this.domainId),
        username = Username.of(this.username),
        password = Password.of(this.passwordHash),
        email = Email.of(this.email),
        name = Name.of(this.name),
        phoneNumber = this.phoneNumber?.let { PhoneNumber.of(it) },
        userType = UserType.valueOf(this.userType.name),
        status = UserStatus.valueOf(this.status.name),
        addresses = this.addresses.map { it.toDomain() },
        defaultAddressId = this.addresses.find { it.isDefault }?.let { AddressId.of(it.addressId) },
        version = this.version,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

// User Domain -> UserEntity
fun User.toEntity(): UserEntity {
    // Map domain to entity, addresses handled as immutable list
    val addressEntities = this.addresses.map { address ->
        address.toEntity() // userEntity will be set by JPA relationship
    }
    return UserEntity(
        domainId = this.id.value,
        username = this.username.value,
        passwordHash = this.password.value,
        email = this.email.value,
        name = this.name.value,
        phoneNumber = this.phoneNumber?.value,
        userType = this.userType,
        status = this.status,
        addresses = addressEntities,
        version = this.version,
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )
}
