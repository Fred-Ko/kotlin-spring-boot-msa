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
            domainId =
                this.id?.value
                    ?: throw IllegalArgumentException(
                        "User Domain ID cannot be null for entity conversion",
                    ), // Use UUID from Domain
            email = email.value,
            password = password.encodedValue,
            name = name.value,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    // Set addresses manually, establishing the bidirectional link
    this.addresses.map { address ->
        // 람다 파라미터를 새 줄로 이동
        address.toEntity().apply { user = entity } // Set back-reference
    }
    // JPA Cascade 설정에 의존하여 저장하므로 별도 설정 불필요
    return entity
}
