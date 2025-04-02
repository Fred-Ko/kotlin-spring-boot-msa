package com.restaurant.presentation.account.v1.dto.request

import jakarta.validation.constraints.NotNull

/**
 * 결제 취소 요청 DTO
 */
data class CancelPaymentRequestV1(
    @field:NotNull(message = "주문 번호는 필수 입력 값입니다.")
    val orderId: String,
)
