package com.restaurant.presentation.account.v1.extensions

import com.restaurant.application.account.query.result.AccountBalanceResult
import com.restaurant.presentation.account.v1.dto.response.AccountBalanceResponseV1

/**
 * 계좌 잔액 결과를 응답 DTO로 변환
 */
fun AccountBalanceResult.toResponse(): AccountBalanceResponseV1 =
    AccountBalanceResponseV1(
        accountId = this.accountId,
        balance = this.balance,
    )
