package com.restaurant.user.presentation.v1.query.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "사용자 주소 상세 응답")
data class AddressDetailResponseV1(
    @Schema(description = "주소 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    val id: String,
    @Schema(description = "주소 이름", example = "집")
    val name: String,
    @Schema(description = "도로명 주소", example = "서울시 강남구 테헤란로 123")
    val streetAddress: String,
    @Schema(description = "상세 주소", example = "456동 789호")
    val detailAddress: String?,
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'", timezone = "UTC")
    @Schema(description = "주소 생성 시간", example = "2023-01-01T12:00:00.000000000Z")
    val createdAt: Instant,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'", timezone = "UTC")
    @Schema(description = "주소 최종 수정 시간", example = "2023-01-01T12:00:00.000000000Z")
    val updatedAt: Instant,
    @Schema(description = "엔티티 버전 (낙관적 락)", example = "1")
    val version: Long,
)
