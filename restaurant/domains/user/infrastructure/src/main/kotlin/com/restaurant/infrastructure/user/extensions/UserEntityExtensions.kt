package com.restaurant.infrastructure.user.extensions

import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.Name
import com.restaurant.domain.user.vo.Password
import com.restaurant.domain.user.vo.UserId
import com.restaurant.infrastructure.user.entity.UserEntity

// UserEntity -> User 변환
fun UserEntity.toDomain(): User {
    // 주소를 도메인 객체로 변환
    val domainAddresses = addresses.map { it.toDomain() }

    return User.reconstitute(
        id = UserId.of(id!!),
        email = Email.of(email),
        password = Password.fromEncoded(password),
        name = Name.of(name),
        addresses = domainAddresses,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

// User -> UserEntity 변환
fun User.toEntity(): UserEntity {
    // UserEntity 생성
    val entity =
        UserEntity(
            id = id?.value,
            email = email.value,
            password = password.encodedValue,
            name = name.value,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    // 주소 추가
    val addressEntities =
        addresses.map { address ->
            address.toEntity().apply { user = entity }
        }

    // 안전한 메서드를 통해 주소 추가
    entity.addAddresses(addressEntities)

    return entity
}
