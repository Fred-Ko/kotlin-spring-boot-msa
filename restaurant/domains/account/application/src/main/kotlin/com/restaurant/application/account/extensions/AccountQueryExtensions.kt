package com.restaurant.application.account.extensions

import com.restaurant.application.account.query.GetAccountBalanceQuery
import com.restaurant.application.account.query.GetAccountTransactionsQuery
import com.restaurant.application.account.query.dto.TransactionDto
import com.restaurant.domain.account.aggregate.Transaction
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.TransactionId

// GetAccountBalanceQuery 확장 함수
fun GetAccountBalanceQuery.toAccountId(): AccountId = AccountId.of(this.accountId)

// GetAccountTransactionsQuery 확장 함수
fun GetAccountTransactionsQuery.toAccountId(): AccountId = AccountId.of(this.accountId)

fun GetAccountTransactionsQuery.toCursor(): TransactionId? = this.cursor?.toLongOrNull()?.let { TransactionId.of(it) }

// Transaction 변환 확장 함수
fun Transaction.toDto(): TransactionDto =
    TransactionDto(
        id = this.id?.value ?: 0,
        accountId = this.accountId?.value ?: 0,
        type = this.type.toString(),
        amount = this.amount.value,
        orderId = this.orderId.value,
        timestamp = this.timestamp,
    )
