package com.restaurant.domain.account.exception

import com.restaurant.domain.account.vo.AccountId

class AccountNotFoundException(
    val accountId: AccountId,
) : AccountDomainException("계좌(${accountId.value})를 찾을 수 없습니다.")
