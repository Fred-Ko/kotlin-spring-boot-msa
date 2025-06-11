package com.restaurant.account.application.command.dto

import com.restaurant.account.domain.vo.UserId

data class CreateAccountCommand(
    val userId: UserId,
)
