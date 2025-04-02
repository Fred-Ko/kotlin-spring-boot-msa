package com.restaurant.presentation.user.v1.dto.request

import jakarta.validation.constraints.NotNull

data class DeleteAddressRequestV1(
    @field:NotNull(message = "주소 ID는 필수입니다.")
    val addressId: Long,
)
