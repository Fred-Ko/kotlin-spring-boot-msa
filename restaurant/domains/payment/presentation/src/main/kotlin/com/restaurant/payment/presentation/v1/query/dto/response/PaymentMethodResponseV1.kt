package com.restaurant.payment.presentation.v1.query.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

/**
 * Payment Method Response DTO V1 (Rule 44, 46)
 * 결제 수단 응답 DTO
 */
@Schema(description = "결제 수단 응답")
data class PaymentMethodResponseV1(
    @JsonProperty("paymentMethodId")
    @Schema(description = "결제 수단 ID", example = "123e4567-e89b-12d3-a456-426614174001")
    val paymentMethodId: String,
    @JsonProperty("userId")
    @Schema(description = "사용자 ID", example = "123e4567-e89b-12d3-a456-426614174002")
    val userId: String,
    @JsonProperty("type")
    @Schema(description = "결제 수단 타입", example = "CREDIT_CARD")
    val type: String,
    @JsonProperty("maskedCardNumber")
    @Schema(description = "마스킹된 카드 번호", example = "****-****-****-1234")
    val maskedCardNumber: String,
    @JsonProperty("cardExpiry")
    @Schema(description = "카드 만료일", example = "12/25")
    val cardExpiry: String,
    @JsonProperty("alias")
    @Schema(description = "결제 수단 별칭", example = "내 주 카드")
    val alias: String,
    @JsonProperty("isDefault")
    @Schema(description = "기본 결제 수단 여부", example = "true")
    val isDefault: Boolean,
    @JsonProperty("isActive")
    @Schema(description = "활성화 여부", example = "true")
    val isActive: Boolean,
    @JsonProperty("createdAt")
    @Schema(description = "생성 일시", example = "2023-12-01T10:00:00Z")
    val createdAt: Instant,
    @JsonProperty("updatedAt")
    @Schema(description = "수정 일시", example = "2023-12-01T10:05:00Z")
    val updatedAt: Instant,
)
