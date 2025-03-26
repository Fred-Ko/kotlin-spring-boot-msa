package com.restaurant.presentation.user.v1.command.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "사용자 탈퇴 요청")
data class UserDeleteRequestV1(
  @field:Schema(description = "현재 비밀번호", example = "password123")
  @field:NotBlank(message = "비밀번호는 필수입니다.")
  val password: String,
)
