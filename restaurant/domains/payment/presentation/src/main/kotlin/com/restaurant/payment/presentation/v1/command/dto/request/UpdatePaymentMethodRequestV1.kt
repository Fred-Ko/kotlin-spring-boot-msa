package com.restaurant.payment.presentation.v1.command.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/**
 * Update Payment Method Request DTO V1 (Rule 44, 46)
 * 결제 수단 수정 요청 DTO
 */
@Schema(description = "결제 수단 수정 요청")
data class UpdatePaymentMethodRequestV1(
    @field:Pattern(
        regexp = "^(0[1-9]|1[0-2])/([0-9]{2})$",
        message = "카드 만료일은 MM/YY 형식이어야 합니다",
    )
    @JsonProperty("cardExpiry")
    @Schema(description = "카드 만료일 (MM/YY)", example = "12/26")
    val cardExpiry: String? = null,
    @field:Size(min = 1, max = 50, message = "별칭은 1-50자 이내여야 합니다")
    @JsonProperty("alias")
    @Schema(description = "결제 수단 별칭", example = "업데이트된 카드")
    val alias: String? = null,
    @JsonProperty("isDefault")
    @Schema(description = "기본 결제 수단 여부", example = "true")
    val isDefault: Boolean? = null,
    @JsonProperty("isActive")
    @Schema(description = "활성화 여부", example = "true")
    val isActive: Boolean? = null,
)
