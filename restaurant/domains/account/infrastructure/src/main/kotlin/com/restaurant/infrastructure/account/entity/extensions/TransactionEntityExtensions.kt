package com.restaurant.infrastructure.account.entity.extensions

import com.restaurant.domain.account.aggregate.Transaction
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.Money
import com.restaurant.domain.account.vo.OrderId
import com.restaurant.domain.account.vo.TransactionId
import com.restaurant.domain.account.vo.TransactionType
import com.restaurant.infrastructure.account.entity.TransactionEntity
import com.restaurant.infrastructure.account.entity.TransactionTypeEntity

/**
 * TransactionEntity -> Transaction 도메인 객체 변환
 */
fun TransactionEntity.toDomain(): Transaction {
    requireNotNull(this.id) { "TransactionEntity ID는 null일 수 없습니다." }
    return Transaction.reconstitute(
        id = TransactionId.of(this.id),
        type = this.type.toDomain(),
        amount = Money.of(this.amount),
        orderId = OrderId.of(this.orderId),
        accountId = AccountId.of(this.accountId),
        cancelled = this.cancelled,
        timestamp = this.timestamp,
    )
}

/**
 * Transaction 도메인 객체 -> TransactionEntity 변환
 */
fun Transaction.toEntity(): TransactionEntity =
    TransactionEntity(
        id = this.id.value,
        accountId = this.accountId.value,
        type = this.type.toEntity(),
        amount = this.amount.value,
        orderId = this.orderId.value,
        cancelled = this.cancelled,
        timestamp = this.timestamp,
    )

/**
 * TransactionTypeEntity Enum -> TransactionType 도메인 Enum 변환
 */
fun TransactionTypeEntity.toDomain(): TransactionType =
    when (this) {
        TransactionTypeEntity.DEBIT -> TransactionType.DEBIT
        TransactionTypeEntity.CREDIT -> TransactionType.CREDIT
    }

/**
 * TransactionType 도메인 Enum -> TransactionTypeEntity Enum 변환
 */
fun TransactionType.toEntity(): TransactionTypeEntity =
    when (this) {
        TransactionType.DEBIT -> TransactionTypeEntity.DEBIT
        TransactionType.CREDIT -> TransactionTypeEntity.CREDIT
    }
