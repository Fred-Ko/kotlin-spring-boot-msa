package com.restaurant.payment.presentation.v1.command.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

/**
 * Refund Payment Request DTO V1 (Rule 44, 46)
 * 결제 환불 요청 DTO
 */
@Schema(description = "결제 환불 요청")
data class RefundPaymentRequestV1(
    @field:NotNull(message = "환불 금액은 필수입니다")
    @field:DecimalMin(value = "0.01", message = "환불 금액은 0.01 이상이어야 합니다")
    @JsonProperty("refundAmount")
    @Schema(description = "환불 금액", example = "5000.00")
    val refundAmount: BigDecimal,
    @JsonProperty("reason")
    @Schema(description = "환불 사유", example = "고객 요청")
    val reason: String? = null,
)
