package com.restaurant.presentation.account.v1.dto.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

/**
 * 결제 처리 요청 DTO
 */
data class ProcessPaymentRequestV1(
    @field:NotNull(message = "주문 번호는 필수 입력 값입니다.")
    val orderId: String,
    @field:NotNull(message = "결제 금액은 필수 입력 값입니다.")
    @field:Min(value = 1, message = "결제 금액은 1 이상이어야 합니다.")
    val amount: BigDecimal,
)
