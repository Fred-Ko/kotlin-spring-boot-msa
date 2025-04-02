package com.restaurant.presentation.user.v1.dto.request

import jakarta.validation.constraints.NotBlank

data class UpdateProfileRequestV1(
    @field:NotBlank(message = "이름은 필수입니다.")
    val name: String,
)
