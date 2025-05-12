package com.restaurant.user.presentation.v1.command.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateAddressRequestV1(
    @field:Schema(description = "도로명 주소", example = "테헤란로 123")
    @field:NotBlank(message = "도로명 주소는 필수 입력 항목입니다.")
    val street: String,
    @field:Schema(description = "상세 주소", example = "456호")
    val detail: String?,
    @field:Schema(description = "우편번호", example = "12345")
    @field:NotBlank(message = "우편번호는 필수 입력 항목입니다.")
    @field:Size(min = 5, max = 5, message = "우편번호는 5자리여야 합니다.")
    val zipCode: String,
    @field:Schema(description = "기본 주소 여부", example = "true")
    val isDefault: Boolean? = false,
)
