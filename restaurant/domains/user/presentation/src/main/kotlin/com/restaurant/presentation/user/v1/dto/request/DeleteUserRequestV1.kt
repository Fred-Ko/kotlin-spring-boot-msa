package com.restaurant.presentation.user.v1.dto.request

import jakarta.validation.constraints.NotBlank

data class DeleteUserRequestV1(
    @field:NotBlank(message = "비밀번호는 필수입니다.")
    val password: String,
)
