package com.restaurant.application.account.extensions

import com.restaurant.application.account.query.GetAccountBalanceQuery
import com.restaurant.domain.account.vo.AccountId

// GetAccountBalanceQuery 확장 함수
fun GetAccountBalanceQuery.toAccountId(): AccountId = AccountId.of(this.accountId)
