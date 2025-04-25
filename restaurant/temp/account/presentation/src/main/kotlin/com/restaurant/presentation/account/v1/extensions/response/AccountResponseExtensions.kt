package com.restaurant.presentation.account.v1.extensions.response

import com.restaurant.application.account.query.dto.AccountBalanceDto
import com.restaurant.presentation.account.v1.dto.response.AccountBalanceResponseV1

/**
 * 계좌 잔액 DTO -> 응답 변환
 */
fun AccountBalanceDto.toResponse(): AccountBalanceResponseV1 =
    AccountBalanceResponseV1(
        accountId = this.accountId,
        balance = this.balance,
    )
