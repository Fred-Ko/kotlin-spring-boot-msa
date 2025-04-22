package com.restaurant.infrastructure.user.extensions

import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.Name
import com.restaurant.domain.user.vo.Password
import com.restaurant.domain.user.vo.UserId
import com.restaurant.infrastructure.user.entity.UserEntity

// UserEntity -> User 변환
fun UserEntity.toDomain(): User {
    // addresses는 Lazy Loading이므로 트랜잭션 경계 내에서만 접근해야 함
    val domainAddresses = this.addresses.map { it.toDomain() } // addresses getter is already List

    return User.reconstitute(
        id = UserId.of(this.domainId), // Use domainId for UserId
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
    val entity =
        UserEntity(
            id = null, // Let JPA handle the Long ID generation
            domainId = this.id.value,
            email = email.value,
            password = password.encodedValue,
            name = name.value,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    // UserEntity의 컬렉션 관리 메서드 사용
    entity.setAddresses(this.addresses.map { it.toEntity() })
    return entity
}
