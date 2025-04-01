package com.restaurant.domain.account.exception

import com.restaurant.common.core.exception.DomainException
import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.Money

class InsufficientBalanceException(
    val accountId: AccountId,
    val currentBalance: Money,
    val requiredAmount: Money,
) : DomainException("계좌(${accountId.value})의 잔액(${currentBalance.amount})이 부족합니다. 필요 금액: ${requiredAmount.amount}")
