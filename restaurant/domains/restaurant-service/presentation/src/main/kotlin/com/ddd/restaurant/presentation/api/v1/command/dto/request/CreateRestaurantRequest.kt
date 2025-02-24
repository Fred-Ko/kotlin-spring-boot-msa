package com.ddd.restaurant.presentation.api.v1.command.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalTime

data class CreateRestaurantRequest(
        @field:NotBlank(message = "이름은 필수 입력값입니다") val name: String,
        @field:NotNull(message = "주소는 필수 입력값입니다") val address: RestaurantAddressDto,
        val menuItems: List<MenuItemDto>,
        @field:NotNull(message = "영업 시간은 필수 입력값입니다") val operationHours: RestaurantOperationHoursDto
) {
        data class RestaurantAddressDto(
                @field:NotBlank(message = "주소는 필수 입력값입니다") val street: String,
                @field:NotBlank(message = "시/도는 필수 입력값입니다") val city: String,
                @field:NotBlank(message = "우편번호는 필수 입력값입니다") val zipCode: String
        )

        data class MenuItemDto(
                @field:NotBlank(message = "메뉴 이름은 필수 입력값입니다") val name: String,
                @field:NotNull(message = "메뉴 가격은 필수 입력값입니다") val price: BigDecimal,
                @field:NotNull(message = "메뉴 수량은 필수 입력값입니다") val quantity: Int
        )

        data class RestaurantOperationHoursDto(
                @field:NotNull(message = "영업 시작 시간은 필수 입력값입니다") val startTime: LocalTime,
                @field:NotNull(message = "영업 종료 시간은 필수 입력값입니다") val endTime: LocalTime
        )
}
