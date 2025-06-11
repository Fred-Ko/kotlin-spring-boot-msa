package com.restaurant.account.application.command.dto

import com.restaurant.account.domain.vo.UserId

data class DeactivateAccountCommand(
    val userId: UserId,
)
