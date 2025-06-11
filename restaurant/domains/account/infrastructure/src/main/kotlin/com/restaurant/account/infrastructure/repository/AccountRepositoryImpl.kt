package com.restaurant.account.infrastructure.repository

import com.restaurant.account.domain.aggregate.Account
import com.restaurant.account.domain.repository.AccountRepository
import com.restaurant.account.domain.vo.AccountId
import com.restaurant.account.domain.vo.UserId
import com.restaurant.account.infrastructure.entity.AccountEntity
import com.restaurant.account.infrastructure.mapper.AccountEntityMapper
import com.restaurant.account.infrastructure.mapper.DomainEventToOutboxMessageConverter
import com.restaurant.outbox.application.dto.OutboxMessageRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
@Transactional
class AccountRepositoryImpl(
    private val springDataJpaAccountRepository: SpringDataJpaAccountRepository,
    private val outboxMessageRepository: OutboxMessageRepository,
    private val domainEventConverter: DomainEventToOutboxMessageConverter
) : AccountRepository {

    override fun save(account: Account): Account {
        // 도메인 이벤트를 아웃박스 메시지로 변환
        val domainEvents = account.getDomainEvents()
        val outboxMessages = domainEventConverter.convertToOutboxMessages(domainEvents)
        
        // 아웃박스 메시지 저장
        outboxMessageRepository.saveAll(outboxMessages)
        
        // 계좌 엔티티 저장
        val accountEntity = AccountEntityMapper.toEntity(account)
        val savedEntity = springDataJpaAccountRepository.save(accountEntity)
        
        // 도메인 이벤트 클리어
        account.clearDomainEvents()
        
        return AccountEntityMapper.toDomain(savedEntity)
    }

    override fun findByUserId(userId: UserId): Optional<Account> {
        return springDataJpaAccountRepository.findByUserId(userId.value)
            .map { AccountEntityMapper.toDomain(it) }
    }

    override fun findById(accountId: AccountId): Optional<Account> {
        return springDataJpaAccountRepository.findByDomainId(accountId.value)
            .map { AccountEntityMapper.toDomain(it) }
    }
}
