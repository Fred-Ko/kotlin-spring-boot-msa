package com.restaurant.user.infrastructure.persistence.repository

import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.infrastructure.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface SpringDataJpaUserRepository : JpaRepository<UserEntity, Long> {
    @EntityGraph(attributePaths = ["addresses"])
    override fun findById(id: Long): Optional<UserEntity>

    @EntityGraph(attributePaths = ["addresses"])
    fun findByUserId(userId: UUID): UserEntity?

    @EntityGraph(attributePaths = ["addresses"])
    fun findByEmail(email: String): UserEntity?

    @EntityGraph(attributePaths = ["addresses"])
    fun findByUsername(username: String): UserEntity?

    fun existsByEmail(email: String): Boolean

    fun existsByUsername(username: String): Boolean

    @Modifying
    @Query("DELETE FROM UserEntity u WHERE u.domainId = :domainId")
    fun deleteByDomainId(
        @Param("domainId") domainId: UUID,
    )

    fun findByUserIdOrThrow(userId: UUID): UserEntity = findByUserId(userId) ?: throw UserDomainException.User.NotFound(userId.toString())
}
