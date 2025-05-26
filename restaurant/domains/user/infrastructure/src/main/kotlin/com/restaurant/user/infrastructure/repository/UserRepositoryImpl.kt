package com.restaurant.user.infrastructure.repository

import com.restaurant.outbox.application.dto.OutboxMessageRepository
import com.restaurant.user.domain.aggregate.User
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.Email
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.domain.vo.Username
import com.restaurant.user.infrastructure.mapper.DomainEventToOutboxMessageConverter
import com.restaurant.user.infrastructure.mapper.toDomain
import com.restaurant.user.infrastructure.mapper.toEntity
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val springDataJpaUserRepository: SpringDataJpaUserRepository,
    private val outboxMessageRepository: OutboxMessageRepository,
    private val domainEventToOutboxMessageConverter: DomainEventToOutboxMessageConverter,
) : UserRepository {
    override fun findById(id: UserId): User? = springDataJpaUserRepository.findByDomainId(id.value)?.toDomain()

    override fun findByUsername(username: Username): User? = springDataJpaUserRepository.findByUsername(username.value)?.toDomain()

    override fun findByEmail(email: Email): User? = springDataJpaUserRepository.findByEmail(email.value)?.toDomain()

    override fun existsByUsername(username: Username): Boolean = springDataJpaUserRepository.existsByUsername(username.value)

    override fun existsByEmail(email: Email): Boolean = springDataJpaUserRepository.existsByEmail(email.value)

    override fun save(user: User): User {
        val userEntity = user.toEntity()
        // 주의: userEntity의 addresses 내부 AddressEntity들이 userEntity를 참조하도록 설정 필요 (양방향 관계)
        // User.toEntity() 내부 또는 여기서 명시적으로 설정
        userEntity.addresses.forEach { it.user = userEntity }

        val savedEntity = springDataJpaUserRepository.save(userEntity)

        // 도메인 이벤트 처리
        val domainEvents = user.getDomainEvents()
        if (domainEvents.isNotEmpty()) {
            val outboxMessages = domainEvents.map { domainEventToOutboxMessageConverter.convert(it) }
            if (outboxMessages.isNotEmpty()) {
                outboxMessageRepository.saveAll(outboxMessages)
            }
            user.clearDomainEvents()
        }

        return savedEntity.toDomain()
    }

    override fun delete(user: User) {
        springDataJpaUserRepository.delete(user.toEntity())
    }
}
