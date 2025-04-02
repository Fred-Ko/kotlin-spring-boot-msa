package com.restaurant.infrastructure.account.repository

import com.restaurant.domain.account.entity.Transaction
import com.restaurant.domain.account.repository.TransactionRepository
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.OrderId
import com.restaurant.domain.account.vo.TransactionType
import com.restaurant.infrastructure.account.entity.TransactionTypeEntity
import com.restaurant.infrastructure.account.entity.extensions.toDomain
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

/**
 * 계좌 트랜잭션 리포지토리 구현체
 */
@Component
class TransactionRepositoryImpl(
    private val jpaTransactionRepository: JpaTransactionRepository,
) : TransactionRepository {
    override fun findByAccountId(
        accountId: AccountId,
        pageable: Pageable,
    ): Page<Transaction> =
        jpaTransactionRepository
            .findByAccountId(accountId.value, pageable)
            .map { it.toDomain() }

    override fun findByOrderId(orderId: OrderId): List<Transaction> =
        jpaTransactionRepository
            .findByOrderId(orderId.value)
            .map { it.toDomain() }

    override fun findByAccountIdAndType(
        accountId: AccountId,
        type: TransactionType,
        pageable: Pageable,
    ): Page<Transaction> {
        val typeEntity =
            when (type) {
                TransactionType.DEBIT -> TransactionTypeEntity.DEBIT
                TransactionType.CREDIT -> TransactionTypeEntity.CREDIT
            }
        return jpaTransactionRepository
            .findByAccountIdAndType(accountId.value, typeEntity, pageable)
            .map { it.toDomain() }
    }

    override fun findByAccountIdAndTimestampBetween(
        accountId: AccountId,
        startTime: Long,
        endTime: Long,
        pageable: Pageable,
    ): Page<Transaction> =
        jpaTransactionRepository
            .findByAccountIdAndTimestampBetween(
                accountId.value,
                startTime,
                endTime,
                pageable,
            ).map { it.toDomain() }
}
