package com.restaurant.user.presentation.v1.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class UpdateProfileRequestV1(
    @field:Schema(description = "사용자 이름", example = "홍길동")
    @field:NotBlank(message = "이름은 필수 입력 항목입니다.")
    val name: String,
)
