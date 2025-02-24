package com.ddd.restaurant.presentation.api.v1.command.dto.request

import jakarta.validation.constraints.NotBlank

data class ChangeRestaurantInfoRequest(
        val newName: String?,
        val newAddress: RestaurantAddressDto?
) {
        data class RestaurantAddressDto(
                @field:NotBlank(message = "주소는 필수 입력값입니다") val street: String,
                @field:NotBlank(message = "시/도는 필수 입력값입니다") val city: String,
                @field:NotBlank(message = "우편번호는 필수 입력값입니다") val zipCode: String
        )
}
