package com.restaurant.presentation.user.v1.dto.request

import jakarta.validation.constraints.NotBlank

data class UpdateAddressRequestV1(
    @field:NotBlank(message = "도로명 주소는 필수입니다.")
    val street: String,
    val detail: String,
    @field:NotBlank(message = "우편번호는 필수입니다.")
    val zipCode: String,
    val isDefault: Boolean,
)
