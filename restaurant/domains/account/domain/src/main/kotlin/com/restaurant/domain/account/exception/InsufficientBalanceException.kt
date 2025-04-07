package com.restaurant.domain.account.exception

import com.restaurant.domain.account.vo.AccountId
import com.restaurant.domain.account.vo.Money

class InsufficientBalanceException(
    val accountId: AccountId,
    val balance: Money,
    val requested: Money,
) : AccountDomainException("계좌(${accountId.value})의 잔액(${balance.value})이 요청 금액(${requested.value})보다 부족합니다.")
