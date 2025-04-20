package com.restaurant.infrastructure.user.repository

import com.restaurant.infrastructure.user.entity.UserEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface SpringDataJpaUserRepository : JpaRepository<UserEntity, Long> {
    @EntityGraph(attributePaths = ["addresses"])
    override fun findById(id: Long): Optional<UserEntity>

    @EntityGraph(attributePaths = ["addresses"])
    fun findByDomainId(domainId: UUID): UserEntity?

    @EntityGraph(attributePaths = ["addresses"])
    fun findByEmail(email: String): UserEntity?

    fun existsByEmail(email: String): Boolean
}
