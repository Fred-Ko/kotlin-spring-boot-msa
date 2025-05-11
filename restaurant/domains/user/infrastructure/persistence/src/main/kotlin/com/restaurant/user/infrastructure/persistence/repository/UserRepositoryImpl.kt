package com.restaurant.user.infrastructure.persistence.repository

import com.restaurant.user.domain.aggregate.User
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.Email
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.domain.vo.Username
import com.restaurant.user.infrastructure.persistence.extensions.toDomain
import com.restaurant.user.infrastructure.persistence.extensions.toEntity
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val springDataJpaUserRepository: SpringDataJpaUserRepository,
    // private val outboxMessageRepository: OutboxMessageRepository, // Outbox 관련 의존성은 Rule 84, 139에 따라 필요시 주입
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

        // Rule 85, 139: 도메인 이벤트 처리 및 Outbox 저장 로직 (아래는 예시이며, 실제 구현 필요)
        // val domainEvents = user.getDomainEvents()
        // if (domainEvents.isNotEmpty()) {
        //     val outboxMessages = domainEvents.map { event ->
        //         // DomainEventAvroSerializer, OutboxMessageFactory 등을 사용하여 OutboxMessage 생성
        //         // 예: OutboxMessage(payload = serialize(event), topic = "user-events", headers = mapOf(...))
        //     }
        //     outboxMessageRepository.saveAll(outboxMessages) // Outbox 저장
        //     user.clearDomainEvents() // 이벤트 클리어
        // }
        return savedEntity.toDomain()
    }

    override fun delete(user: User) {
        springDataJpaUserRepository.delete(user.toEntity())
    }
}
