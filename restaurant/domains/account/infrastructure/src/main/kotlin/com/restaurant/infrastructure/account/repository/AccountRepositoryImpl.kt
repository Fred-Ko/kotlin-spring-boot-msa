package com.restaurant.infrastructure.account.repository

import com.restaurant.domain.account.aggregate.Account
import com.restaurant.domain.account.repository.AccountRepository
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.UserId
import com.restaurant.infrastructure.account.entity.extensions.toDomain
import com.restaurant.infrastructure.account.entity.extensions.toEntity
import org.springframework.stereotype.Component

/**
 * 계좌 리포지토리 구현체
 */
@Component
class AccountRepositoryImpl(
    private val jpaAccountRepository: JpaAccountRepository,
) : AccountRepository {
    override fun findById(id: AccountId): Account? = jpaAccountRepository.findById(id.value).map { it.toDomain() }.orElse(null)

    override fun findByUserId(userId: UserId): Account? = jpaAccountRepository.findByUserId(userId.value)?.toDomain()

    override fun save(account: Account): Account {
        val entity = account.toEntity()
        val savedEntity = jpaAccountRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun delete(id: AccountId) {
        jpaAccountRepository.deleteById(id.value)
    }
}
