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
    // 기본 UserEntity 생성
    val entity =
        UserEntity(
            id = id?.value,
            email = email.value,
            password = password.encodedValue,
            name = name.value,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    // 주소 생성 및 연결
    if (addresses.isNotEmpty()) {
        // 각 주소를 엔티티로 변환
        val addressEntities =
            addresses.map { address ->
                address.toEntity()
            }

        // 함수형 스타일로 주소들 추가
        return entity.withAddresses(addressEntities)
    }

    return entity
}
