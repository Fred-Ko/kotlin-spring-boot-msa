package com.restaurant.payment.presentation.v1.command.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import java.math.BigDecimal

/**
 * Process Payment Request DTO V1 (Rule 44, 46)
 * 결제 처리 요청 DTO
 */
@Schema(description = "결제 처리 요청")
data class ProcessPaymentRequestV1(
    @field:NotBlank(message = "결제 수단 ID는 필수입니다")
    @field:Pattern(
        regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
        message = "결제 수단 ID는 UUID 형식이어야 합니다",
    )
    @JsonProperty("paymentMethodId")
    @Schema(description = "결제 수단 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440002")
    val paymentMethodId: String,
    @field:NotNull(message = "결제 금액은 필수입니다")
    @field:DecimalMin(value = "0.01", message = "결제 금액은 0.01 이상이어야 합니다")
    @JsonProperty("amount")
    @Schema(description = "결제 금액", example = "10000.00")
    val amount: BigDecimal,
    @JsonProperty("description")
    @Schema(description = "결제 설명", example = "음식 주문 결제")
    val description: String? = null,
)
