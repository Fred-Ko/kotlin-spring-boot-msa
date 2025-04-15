package com.restaurant.infrastructure.account.repository

import com.restaurant.domain.account.aggregate.Transaction
import com.restaurant.domain.account.repository.TransactionRepository
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.OrderId
import com.restaurant.domain.account.vo.TransactionId
import com.restaurant.domain.account.vo.TransactionType
import com.restaurant.infrastructure.account.entity.extensions.toDomain
import com.restaurant.infrastructure.account.entity.extensions.toEntity
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

/**
 * 트랜잭션 리포지토리 구현체
 */
@Component
class TransactionRepositoryImpl(
    private val jpaTransactionRepository: JpaTransactionRepository,
) : TransactionRepository {
    override fun findById(id: TransactionId): Transaction? = jpaTransactionRepository.findById(id.value).map { it.toDomain() }.orElse(null)

    override fun save(transaction: Transaction): Transaction {
        val entity = transaction.toEntity()
        val savedEntity = jpaTransactionRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun findByAccountIdWithCursor(
        accountId: AccountId,
        cursor: TransactionId?,
        limit: Int,
    ): List<Transaction> {
        val pageable = PageRequest.of(0, limit)
        return jpaTransactionRepository
            .findByAccountIdWithCursor(
                accountId.value,
                cursor?.value,
                pageable,
            ).map { it.toDomain() }
    }

    override fun findByOrderId(orderId: OrderId): List<Transaction> =
        jpaTransactionRepository
            .findByOrderId(orderId.value)
            .map { it.toDomain() }

    override fun findByAccountIdAndTypeWithCursor(
        accountId: AccountId,
        type: TransactionType,
        cursor: TransactionId?,
        limit: Int,
    ): List<Transaction> {
        val entityType = type.toEntity()

        val pageable = PageRequest.of(0, limit)
        return jpaTransactionRepository
            .findByAccountIdAndTypeWithCursor(
                accountId.value,
                entityType,
                cursor?.value,
                pageable,
            ).map { it.toDomain() }
    }

    override fun findByAccountIdAndTimestampBetweenWithCursor(
        accountId: AccountId,
        startTime: Long,
        endTime: Long,
        cursor: TransactionId?,
        limit: Int,
    ): List<Transaction> {
        val pageable = PageRequest.of(0, limit)
        return jpaTransactionRepository
            .findByAccountIdAndTimestampBetweenWithCursor(
                accountId.value,
                startTime,
                endTime,
                cursor?.value,
                pageable,
            ).map { it.toDomain() }
    }
}
