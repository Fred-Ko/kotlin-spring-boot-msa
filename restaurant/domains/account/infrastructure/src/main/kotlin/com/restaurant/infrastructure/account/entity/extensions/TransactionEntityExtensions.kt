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
fun TransactionEntity.toDomain(): Transaction =
    Transaction(
        id = this.id?.let { TransactionId.of(it) },
        type =
            when (this.type) {
                TransactionTypeEntity.DEBIT -> TransactionType.DEBIT
                TransactionTypeEntity.CREDIT -> TransactionType.CREDIT
            },
        amount = Money.of(this.amount),
        orderId = OrderId.of(this.orderId),
        accountId = AccountId.of(this.accountId),
        timestamp = this.timestamp,
    )

/**
 * Transaction 도메인 객체 -> TransactionEntity 변환
 */
fun Transaction.toEntity(): TransactionEntity =
    TransactionEntity(
        id = this.id?.value,
        accountId = this.accountId?.value ?: throw IllegalStateException("계좌 ID가 없습니다."),
        type =
            when (this.type) {
                TransactionType.DEBIT -> TransactionTypeEntity.DEBIT
                TransactionType.CREDIT -> TransactionTypeEntity.CREDIT
            },
        amount = this.amount.value,
        orderId = this.orderId.value,
        timestamp = this.timestamp,
    )
