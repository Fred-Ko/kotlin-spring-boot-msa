package com.restaurant.user.presentation.v1.command.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

@Schema(description = "사용자 프로필 수정 요청")
data class UpdateProfileRequestV1(
    @field:Schema(description = "사용자 이름", example = "홍길동")
    @field:NotBlank(message = "이름은 필수 입력 항목입니다.")
    val name: String,
    @field:Schema(description = "전화번호 (선택)", example = "010-1234-5678", nullable = true)
    @field:Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "유효한 전화번호 형식이 아닙니다 (예: 010-1234-5678)")
    val phoneNumber: String? = null,
)
