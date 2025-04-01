package com.restaurant.presentation.account.v1.dto.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

/**
 * 결제 처리 요청 DTO
 */
data class ProcessPaymentRequestV1(
    @field:NotNull(message = "주문 ID는 필수입니다.")
    val orderId: Long,
    @field:NotNull(message = "결제 금액은 필수입니다.")
    @field:Min(1, message = "결제 금액은 1 이상이어야 합니다.")
    val amount: BigDecimal,
)
