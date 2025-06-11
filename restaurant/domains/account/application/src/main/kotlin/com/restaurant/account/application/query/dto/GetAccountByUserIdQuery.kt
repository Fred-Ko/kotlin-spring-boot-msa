package com.restaurant.account.application.query.dto

import com.restaurant.account.domain.vo.UserId

data class GetAccountByUserIdQuery(
    val userId: UserId,
)
