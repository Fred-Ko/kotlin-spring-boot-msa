package com.restaurant.presentation.user.v1.command.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class UserAddressRegisterRequestV1(
  @field:NotBlank(message = "도로명 주소는 필수입니다.")
  @Schema(description = "도로명 주소", example = "서울시 강남구 테헤란로 123")
  val street: String,
  @Schema(description = "상세 주소", example = "456동 789호")
  val detail: String = "",
  @field:NotBlank(message = "우편번호는 필수입니다.")
  @field:Pattern(
    regexp = "^\\d{5}$",
    message = "우편번호는 5자리 숫자여야 합니다.",
  )
  @Schema(description = "우편번호", example = "12345")
  val zipCode: String,
  @Schema(description = "기본 주소 여부", example = "false", defaultValue = "false")
  val isDefault: Boolean = false,
)
