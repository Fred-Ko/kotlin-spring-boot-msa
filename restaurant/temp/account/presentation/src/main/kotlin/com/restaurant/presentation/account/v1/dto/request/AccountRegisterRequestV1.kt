package com.restaurant.presentation.account.v1.dto.request

import jakarta.validation.constraints.Min
import java.math.BigDecimal

/**
 * 계좌 등록 요청 DTO
 */
data class AccountRegisterRequestV1(
    @field:Min(value = 0, message = "초기 잔액은 0 이상이어야 합니다.")
    val initialBalance: BigDecimal,
)
