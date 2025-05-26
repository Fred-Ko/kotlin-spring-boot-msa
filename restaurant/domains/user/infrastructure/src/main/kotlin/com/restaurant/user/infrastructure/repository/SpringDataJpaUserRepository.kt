package com.restaurant.user.infrastructure.repository

import com.restaurant.user.infrastructure.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SpringDataJpaUserRepository : JpaRepository<UserEntity, Long> {
    fun findByDomainId(domainId: UUID): UserEntity?

    fun findByUsername(username: String): UserEntity?

    fun findByEmail(email: String): UserEntity?

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean
}
