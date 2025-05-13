package com.restaurant.user.infrastructure.persistence.repository

import com.restaurant.user.domain.aggregate.User
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.Email
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.domain.vo.Username
import com.restaurant.user.infrastructure.persistence.extensions.toDomain
import com.restaurant.user.infrastructure.persistence.extensions.toEntity
import org.springframework.stereotype.Repository

import com.restaurant.outbox.application.port.OutboxMessageRepository
import com.restaurant.user.infrastructure.messaging.serialization.OutboxMessageFactory

@Repository
class UserRepositoryImpl(
    private val springDataJpaUserRepository: SpringDataJpaUserRepository,
    private val outboxMessageRepository: OutboxMessageRepository,
    private val outboxMessageFactory: OutboxMessageFactory
) : UserRepository {
    override fun findById(id: UserId): User? {
        return springDataJpaUserRepository.findByDomainId(id.value)?.toDomain()
    }

    override fun findByUsername(username: Username): User? {
        return springDataJpaUserRepository.findByUsernameValue(username.value)?.toDomain()
    }

    override fun findByEmail(email: Email): User? {
        return springDataJpaUserRepository.findByEmailValue(email.value)?.toDomain()
    }

    override fun existsByUsername(username: Username): Boolean {
        return springDataJpaUserRepository.existsByUsernameValue(username.value)
    }

    override fun existsByEmail(email: Email): Boolean {
        return springDataJpaUserRepository.existsByEmailValue(email.value)
    }

    override fun save(user: User): User {
        val userEntity = user.toEntity()
        val savedEntity = springDataJpaUserRepository.save(userEntity)

        val domainEvents = user.getDomainEvents()
        if (domainEvents.isNotEmpty()) {
            val outboxMessages = domainEvents.flatMap { outboxMessageFactory.createMessagesFromEvent(it) }
            outboxMessageRepository.saveAll(outboxMessages)
            user.clearDomainEvents()
        }
        return savedEntity.toDomain()
    }

    override fun delete(user: User) {
        springDataJpaUserRepository.delete(user.toEntity())
    }
}
