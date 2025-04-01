package com.restaurant.infrastructure.account.entity.extensions

import com.restaurant.domain.account.aggregate.Account
import com.restaurant.domain.account.aggregate.Transaction
import com.restaurant.domain.account.aggregate.TransactionType
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.Money
import com.restaurant.domain.account.vo.OrderId
import com.restaurant.domain.account.vo.UserId
import com.restaurant.infrastructure.account.entity.AccountEntity
import com.restaurant.infrastructure.account.entity.TransactionEntity
import com.restaurant.infrastructure.account.entity.TransactionTypeEntity

/**
 * AccountEntity -> Account 도메인 객체 변환
 */
fun AccountEntity.toDomain(): Account {
    val transactions = this.transactions.map { it.toDomain() }

    return Account(
        id = this.id?.let { AccountId.of(it) },
        userId = UserId.of(this.userId),
        balance = Money.of(this.balance),
        transactions = transactions,
    )
}

/**
 * Account 도메인 객체 -> AccountEntity 변환
 */
fun Account.toEntity(): AccountEntity {
    val entity =
        AccountEntity(
            id = this.id?.value,
            userId = this.userId.value,
            balance = this.balance.amount,
        )

    // 트랜잭션이 있으면 추가
    this.transactions.forEach { transaction ->
        entity.transactions.add(
            TransactionEntity(
                account = entity,
                type =
                    when (transaction.type) {
                        TransactionType.DEBIT -> TransactionTypeEntity.DEBIT
                        TransactionType.CREDIT -> TransactionTypeEntity.CREDIT
                    },
                amount = transaction.amount.amount,
                orderId = transaction.orderId.value,
                timestamp = transaction.timestamp,
            ),
        )
    }

    return entity
}

/**
 * TransactionEntity -> Transaction 도메인 객체 변환
 */
fun TransactionEntity.toDomain(): Transaction =
    Transaction(
        type =
            when (this.type) {
                TransactionTypeEntity.DEBIT -> TransactionType.DEBIT
                TransactionTypeEntity.CREDIT -> TransactionType.CREDIT
            },
        amount = Money.of(this.amount),
        orderId = OrderId.of(this.orderId),
        timestamp = this.timestamp,
    )
