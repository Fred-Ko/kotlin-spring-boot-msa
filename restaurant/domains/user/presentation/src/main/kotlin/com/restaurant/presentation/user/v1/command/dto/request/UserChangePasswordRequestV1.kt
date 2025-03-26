package com.restaurant.presentation.user.v1.command.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "비밀번호 변경 요청")
data class UserChangePasswordRequestV1(
  @field:Schema(description = "현재 비밀번호", example = "current123")
  @field:NotBlank(message = "현재 비밀번호는 필수입니다.")
  val currentPassword: String,
  @field:Schema(description = "새 비밀번호", example = "newpass456")
  @field:NotBlank(message = "새 비밀번호는 필수입니다.")
  @field:Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
  val newPassword: String,
)
