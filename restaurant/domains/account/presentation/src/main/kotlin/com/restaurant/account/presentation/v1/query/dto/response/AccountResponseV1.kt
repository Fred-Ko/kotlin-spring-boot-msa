package com.restaurant.account.presentation.v1.query.dto.response

import com.restaurant.account.domain.aggregate.AccountStatus
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class AccountResponseV1(
    val accountId: UUID,
    val userId: UUID,
    val balance: BigDecimal,
    val status: AccountStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
)
