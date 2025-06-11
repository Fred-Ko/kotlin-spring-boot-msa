package com.restaurant.account.application.query.usecase

import com.restaurant.account.application.query.dto.AccountDto
import com.restaurant.account.application.query.dto.GetAccountByUserIdQuery

fun interface GetAccountByUserIdUseCase {
    fun getAccountByUserId(query: GetAccountByUserIdQuery): AccountDto?
}
