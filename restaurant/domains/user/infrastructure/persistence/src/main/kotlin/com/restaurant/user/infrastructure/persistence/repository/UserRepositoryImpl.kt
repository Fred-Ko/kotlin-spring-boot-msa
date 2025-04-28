package com.restaurant.user.infrastructure.persistence.repository

import com.restaurant.common.config.filter.CorrelationIdFilter
import com.restaurant.outbox.port.OutboxMessageRepository
import com.restaurant.user.domain.aggregate.User
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.Email
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.domain.vo.Username
import com.restaurant.user.infrastructure.persistence.entity.UserEntity
import com.restaurant.user.infrastructure.persistence.extensions.toDomain
import com.restaurant.user.infrastructure.persistence.extensions.toEntity
import com.restaurant.user.infrastructure.persistence.repository.SpringDataJpaUserRepository
import com.restaurant.user.infrastructure.messaging.serialization.OutboxMessageFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import org.slf4j.MDC
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import java.util.UUID

private val log = KotlinLogging.logger {}

@Repository
class UserRepositoryImpl(
    private val springDataJpaUserRepository: SpringDataJpaUserRepository,
    private val outboxRepository: OutboxMessageRepository,
    private val outboxMessageFactory: OutboxMessageFactory
) : UserRepository {
    @Transactional
    override fun save(user: User): User {
        val isNew = user.version == 0L
        val correlationId = MDC.get(CorrelationIdFilter.CORRELATION_ID_MDC_KEY) ?: "SYSTEM-${UUID.randomUUID()}"
        log.debug { "Attempting to save user: ${user.id.value}, isNew=$isNew, correlationId=$correlationId" }

        val entity: UserEntity = user.toEntity()
        val savedEntity: UserEntity = try {
            springDataJpaUserRepository.saveAndFlush(entity)
        } catch (e: DataIntegrityViolationException) {
            log.warn(e) { "Data integrity violation while saving user ${user.id.value}" }
            val message = e.mostSpecificCause.message ?: ""
            when {
                message.contains("uc_users_email", ignoreCase = true) || message.contains("users_email_key") || message.contains("uk_user_email") ->
                    throw UserDomainException.User.DuplicateEmail(user.email.value)
                message.contains("uc_users_username", ignoreCase = true) || message.contains("users_username_key") || message.contains("uk_user_username") ->
                    throw UserDomainException.User.DuplicateUsername(user.username.value)
                else -> throw UserDomainException.PersistenceError("Data integrity violation", e)
            }
        } catch (e: Exception) {
            log.error(e) { "Failed to save user ${user.id.value}" }
            throw UserDomainException.PersistenceError("Failed to save user", e)
        }

        val events = user.getDomainEvents()
        if (events.isNotEmpty()) {
            log.info { "Processing ${events.size} domain event(s) for aggregate ${user.id.value} with correlationId $correlationId" }
            try {
                val outboxMessages = events.flatMap { event ->
                    outboxMessageFactory.createMessagesFromEvent(event, correlationId)
                }

                if (outboxMessages.isNotEmpty()) {
                    outboxRepository.save(outboxMessages)
                    log.info { "Saved ${outboxMessages.size} message(s) to outbox for aggregate ${user.id.value}" }
                }
            } catch (ex: Exception) {
                log.error(ex) {
                    "Failed to process/save ${events.size} event(s) to outbox for aggregate ${user.id.value}. Rethrowing for transaction rollback."
                }
                throw UserDomainException.PersistenceError("Failed to save domain events to outbox", ex)
            } finally {
                user.clearDomainEvents()
            }
        }

        return savedEntity.toDomain()
    }

    @Transactional(readOnly = true)
    override fun findById(id: UserId): User? {
        log.debug { "Finding user by ID: ${id.value}" }
        return springDataJpaUserRepository
            .findByDomainId(id.value)
            ?.toDomain()
            .also { if (it == null) log.warn { "User not found for ID: ${id.value}" } }
    }

    @Transactional(readOnly = true)
    override fun findByUsername(username: Username): User? {
        log.debug { "Finding user by username: ${username.value}" }
        return springDataJpaUserRepository
            .findByUsername(username.value)
            ?.toDomain()
            .also { if (it == null) log.warn { "User not found for Username: ${username.value}" } }
    }

    @Transactional(readOnly = true)
    override fun findByEmail(email: Email): User? {
        log.debug { "Finding user by email: ${email.value}" }
        return springDataJpaUserRepository
            .findByEmailValue(email.value)
            ?.toDomain()
            .also { if (it == null) log.warn { "User not found for Email: ${email.value}" } }
    }

    @Transactional(readOnly = true)
    override fun existsByUsername(username: Username): Boolean {
        log.debug { "Checking existence by username: ${username.value}" }
        return springDataJpaUserRepository.existsByUsername(username.value)
    }

    @Transactional(readOnly = true)
    override fun existsByEmail(email: Email): Boolean {
        log.debug { "Checking existence by email: ${email.value}" }
        return springDataJpaUserRepository.existsByEmailValue(email.value)
    }
}
