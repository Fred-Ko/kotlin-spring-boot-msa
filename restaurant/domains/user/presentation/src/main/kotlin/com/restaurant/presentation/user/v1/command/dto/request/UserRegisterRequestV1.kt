package com.restaurant.presentation.user.v1.command.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "사용자 등록 요청")
data class UserRegisterRequestV1(
  @field:Schema(description = "사용자 이메일", example = "test@example.com")
  @field:NotBlank(message = "이메일은 필수입니다.")
  @field:Email(message = "유효한 이메일 형식이 아닙니다.")
  val email: String,
  @field:Schema(description = "사용자 비밀번호", example = "password123")
  @field:NotBlank(message = "비밀번호는 필수입니다.")
  @field:Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
  val password: String,
  @field:Schema(description = "사용자 이름", example = "홍길동")
  @field:NotBlank(message = "이름은 필수입니다.")
  val name: String,
)
