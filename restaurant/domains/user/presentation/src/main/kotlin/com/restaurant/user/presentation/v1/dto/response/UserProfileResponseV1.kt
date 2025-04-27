package com.restaurant.user.presentation.v1.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import java.time.Instant

@Schema(description = "주소 응답")
data class AddressResponseV1(
    @Schema(description = "주소 ID") val addressId: String,
    @Schema(description = "도로명 주소") val street: String,
    @Schema(description = "상세 주소") val detail: String,
    @Schema(description = "우편번호") val zipCode: String,
    @Schema(description = "기본 주소 여부") val isDefault: Boolean,
)

/**
 * 사용자 프로필 정보 응답 DTO (Rule 1.4, 39)
 */
@Relation(collectionRelation = "users", itemRelation = "user") // For HATEOAS collection relation
@Schema(description = "사용자 프로필 응답")
data class UserProfileResponseV1(
    @Schema(description = "사용자 ID", example = "550e8400-e29b-41d4-a716-446655440000") val id: String,
    @Schema(description = "사용자 이메일", example = "test@example.com") val email: String,
    @Schema(description = "사용자 이름", example = "홍길동") val name: String,
    @Schema(description = "사용자 아이디", example = "testuser") val username: String,
    @Schema(description = "전화번호", example = "010-1234-5678", nullable = true) val phoneNumber: String?,
    @Schema(description = "사용자 타입", example = "CUSTOMER") val userType: String,
    @Schema(description = "주소 목록") val addresses: List<AddressResponseV1> = emptyList(),
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'", timezone = "UTC")
    @Schema(description = "계정 생성 시간", example = "2023-01-01 12:00:00")
    val createdAt: Instant,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'", timezone = "UTC")
    @Schema(description = "계정 최종 수정 시간", example = "2023-01-01 12:00:00")
    val updatedAt: Instant,
) : RepresentationModel<UserProfileResponseV1>() // Inherit from RepresentationModel
