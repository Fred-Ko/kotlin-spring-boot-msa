package com.restaurant.payment.presentation.v1.query.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

/**
 * Get Payment By ID Request DTO V1 (Rule 44, 46)
 * 결제 조회 요청 DTO
 */
@Schema(description = "결제 조회 요청")
data class GetPaymentByIdRequestV1(
    @field:NotBlank(message = "결제 ID는 필수입니다")
    @field:Pattern(
        regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
        message = "유효한 UUID 형식이어야 합니다",
    )
    @Schema(description = "결제 ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val paymentId: String,
)
