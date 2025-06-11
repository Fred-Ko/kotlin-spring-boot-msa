package com.restaurant.account.presentation.v1.query.extensions.dto.response

import com.restaurant.account.application.query.dto.AccountDto
import com.restaurant.account.presentation.v1.query.dto.response.AccountResponseV1

fun AccountDto.toResponseV1(): AccountResponseV1 =
    AccountResponseV1(
        accountId = this.accountId,
        userId = this.userId,
        balance = this.balance,
        status = this.status,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
