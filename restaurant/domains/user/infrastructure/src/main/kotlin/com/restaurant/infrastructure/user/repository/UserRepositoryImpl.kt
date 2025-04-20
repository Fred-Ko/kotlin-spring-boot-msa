package com.restaurant.infrastructure.user.repository

import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.UserId
import com.restaurant.infrastructure.user.extensions.toDomain
import com.restaurant.infrastructure.user.extensions.toEntity
import com.restaurant.shared.outbox.application.port.OutboxEventRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val jpaRepository: SpringDataJpaUserRepository,
    private val outboxEventRepository: OutboxEventRepository,
) : UserRepository {
    override fun save(user: User): User {
        val domainEvents = user.getDomainEvents()

        val entity = user.toEntity()
        val savedEntity = jpaRepository.save(entity)

        if (domainEvents.isNotEmpty()) {
            val aggregateId = user.id?.value?.toString() ?: throw IllegalStateException("User domain ID must not be null for outbox saving")
            val aggregateType = user.javaClass.simpleName
            outboxEventRepository.save(domainEvents, aggregateType, aggregateId)
        }

        user.clearDomainEvents()

        return savedEntity.toDomain()
    }

    override fun findById(id: UserId): User? = jpaRepository.findByDomainId(id.value)?.toDomain()

    override fun findByEmail(email: Email): User? =
        jpaRepository.findByEmail(email.value)?.let {
            it.toDomain()
        }

    override fun existsByEmail(email: Email): Boolean = jpaRepository.existsByEmail(email.value)

    override fun delete(user: User) {
        val entity = user.toEntity()
        jpaRepository.delete(entity)
    }
}
