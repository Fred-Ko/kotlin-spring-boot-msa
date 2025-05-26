package com.restaurant.user.presentation.v1.command.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "사용자 계정 삭제 요청")
data class DeleteUserRequestV1(
    @field:Schema(description = "현재 비밀번호 확인", example = "currentPassword123")
    @field:NotBlank(message = "현재 비밀번호는 필수 입력 항목입니다.")
    @field:Size(min = 8, message = "비밀번호는 최소 8자리 이상이어야 합니다.")
    val currentPassword: String,
)
