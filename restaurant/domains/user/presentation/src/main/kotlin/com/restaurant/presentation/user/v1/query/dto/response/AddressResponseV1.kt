package com.restaurant.presentation.user.v1.query.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class AddressResponseV1(
    @Schema(description = "주소 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    val id: String,
    @Schema(description = "도로명 주소", example = "서울시 강남구 테헤란로 123")
    val street: String,
    @Schema(description = "상세 주소", example = "456동 789호")
    val detail: String,
    @Schema(description = "우편번호", example = "12345")
    val zipCode: String,
    @Schema(description = "기본 주소 여부", example = "true")
    val isDefault: Boolean,
)
