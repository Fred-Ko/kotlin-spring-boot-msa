package com.restaurant.user.presentation.v1.query.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "사용자 주소 응답")
data class AddressResponseV1(
    @Schema(description = "주소 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    val id: String,
    @Schema(description = "주소 이름", example = "집")
    val name: String,
    @Schema(description = "도로명 주소", example = "서울시 강남구 테헤란로 123")
    val street: String,
    @Schema(description = "상세 주소", example = "456동 789호")
    val detail: String,
    @Schema(description = "도시", example = "서울시")
    val city: String,
    @Schema(description = "주/도", example = "서울특별시")
    val state: String,
    @Schema(description = "국가", example = "대한민국")
    val country: String,
    @Schema(description = "우편번호", example = "12345")
    val zipCode: String,
    @Schema(description = "기본 주소 여부", example = "true")
    val isDefault: Boolean,
)
