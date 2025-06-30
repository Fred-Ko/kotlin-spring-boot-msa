package com.restaurant.payment.presentation.v1.query.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.Instant

/**
 * Payment Response DTO V1 (Rule 44, 46)
 * 결제 응답 DTO
 */
@Schema(description = "결제 응답")
data class PaymentResponseV1(
    @JsonProperty("paymentId")
    @Schema(description = "결제 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val paymentId: String,
    @JsonProperty("orderId")
    @Schema(description = "주문 ID", example = "order-123")
    val orderId: String,
    @JsonProperty("amount")
    @Schema(description = "결제 금액", example = "25000.00")
    val amount: BigDecimal,
    @JsonProperty("currency")
    @Schema(description = "통화", example = "KRW")
    val currency: String,
    @JsonProperty("status")
    @Schema(description = "결제 상태", example = "COMPLETED")
    val status: String,
    @JsonProperty("paymentMethodId")
    @Schema(description = "결제 수단 ID", example = "123e4567-e89b-12d3-a456-426614174001")
    val paymentMethodId: String?,
    @JsonProperty("description")
    @Schema(description = "결제 설명", example = "음식 주문 결제")
    val description: String?,
    @JsonProperty("refundedAmount")
    @Schema(description = "환불 금액", example = "0.00")
    val refundedAmount: BigDecimal?,
    @JsonProperty("createdAt")
    @Schema(description = "생성 일시", example = "2023-12-01T10:00:00Z")
    val createdAt: Instant,
    @JsonProperty("updatedAt")
    @Schema(description = "수정 일시", example = "2023-12-01T10:05:00Z")
    val updatedAt: Instant,
)
