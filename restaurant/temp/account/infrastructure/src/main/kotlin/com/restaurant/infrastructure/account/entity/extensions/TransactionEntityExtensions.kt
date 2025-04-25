package com.restaurant.infrastructure.account.entity.extensions

import com.restaurant.domain.account.aggregate.Transaction
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.Money
import com.restaurant.domain.account.vo.OrderId
import com.restaurant.domain.account.vo.TransactionId
import com.restaurant.domain.account.vo.TransactionType
import com.restaurant.infrastructure.account.entity.AccountTransactionTypeEntity
import com.restaurant.infrastructure.account.entity.TransactionEntity
import java.time.Instant

/**
 * TransactionEntity를 도메인 Transaction으로 변환
 */
fun TransactionEntity.toDomain(): Transaction =
    Transaction.reconstitute(
        id = TransactionId.of(id!!),
        type = type.toDomain(),
        amount = Money.of(amount),
        orderId = OrderId.of(orderId),
        accountId = AccountId.of(accountId),
        cancelled = cancelled,
        timestamp = timestamp.toEpochMilli(),
    )

/**
 * Transaction을 TransactionEntity로 변환
 */
fun Transaction.toEntity(): TransactionEntity =
    TransactionEntity(
        id = id.value,
        accountId = accountId.value,
        type = type.toEntity(),
        amount = amount.value,
        orderId = orderId.value,
        cancelled = cancelled,
        timestamp = Instant.ofEpochMilli(timestamp),
    )

/**
 * AccountTransactionTypeEntity를 도메인 TransactionType으로 변환
 */
private fun AccountTransactionTypeEntity.toDomain(): TransactionType =
    when (this) {
        AccountTransactionTypeEntity.DEBIT -> TransactionType.DEBIT
        AccountTransactionTypeEntity.CREDIT -> TransactionType.CREDIT
    }

/**
 * TransactionType을 AccountTransactionTypeEntity로 변환
 */
private fun TransactionType.toEntity(): AccountTransactionTypeEntity =
    when (this) {
        TransactionType.DEBIT -> AccountTransactionTypeEntity.DEBIT
        TransactionType.CREDIT -> AccountTransactionTypeEntity.CREDIT
    }
