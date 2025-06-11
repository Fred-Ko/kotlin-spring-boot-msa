package com.restaurant.account.application.query.dto

import com.restaurant.account.domain.aggregate.AccountStatus
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class AccountDto(
    val accountId: UUID,
    val userId: UUID,
    val balance: BigDecimal,
    val status: AccountStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
)
