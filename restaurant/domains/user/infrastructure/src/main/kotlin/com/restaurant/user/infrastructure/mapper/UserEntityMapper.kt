package com.restaurant.user.infrastructure.mapper

import com.restaurant.user.domain.aggregate.User
import com.restaurant.user.domain.vo.Email
import com.restaurant.user.domain.vo.Name
import com.restaurant.user.domain.vo.Password
import com.restaurant.user.domain.vo.PhoneNumber
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.domain.vo.Username
import com.restaurant.user.infrastructure.entity.UserEntity

fun UserEntity.toDomain(): User {
    val userDomainId = UserId.of(this.domainId)
    val domainAddresses =
        this.addresses
            .map { addressEntity ->
                addressEntity.toDomain() // userId 인자 제거됨
            }.toList()

    // User.reconstitute에서 defaultAddressId를 사용하지 않거나,
    // User 도메인 객체가 addresses 리스트 내 isDefault 플래그로 관리한다고 가정
    return User(
        id = userDomainId,
        username = Username.of(this.username),
        password = Password.of(this.passwordHash),
        email = Email.of(this.email),
        name = Name.of(this.name),
        phoneNumber = this.phoneNumber?.let { PhoneNumber.of(it) },
        userType = this.userType,
        status = this.status,
        addresses = domainAddresses,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        version = this.version,
    )
}

fun User.toEntity(): UserEntity {
    val entity =
        UserEntity(
            domainId = this.id.value,
            username = this.username.value,
            passwordHash = this.password.value,
            email = this.email.value,
            name = this.name.value,
            phoneNumber = this.phoneNumber?.value,
            userType = this.userType,
            status = this.status,
            version = this.version,
        )

    // addresses 변환 및 양방향 관계 설정
    val addressEntities =
        this.addresses
            .map { address ->
                val addressEntity = address.toEntity()
                addressEntity.user = entity // UserEntity에 대한 참조 설정
                addressEntity
            }.toMutableList()
    entity.addresses = addressEntities
    return entity
}
