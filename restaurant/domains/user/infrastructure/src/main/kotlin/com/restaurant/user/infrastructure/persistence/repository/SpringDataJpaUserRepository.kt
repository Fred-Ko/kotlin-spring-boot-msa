package com.restaurant.user.infrastructure.persistence.repository

import com.restaurant.user.infrastructure.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SpringDataJpaUserRepository : JpaRepository<UserEntity, Long> {
    fun findByDomainId(domainId: UUID): UserEntity?

    fun findByUsernameValue(username: String): UserEntity?

    fun findByEmailValue(email: String): UserEntity? // findByEmailValue 메서드 추가

    fun existsByUsernameValue(username: String): Boolean

    fun existsByEmailValue(email: String): Boolean
}
