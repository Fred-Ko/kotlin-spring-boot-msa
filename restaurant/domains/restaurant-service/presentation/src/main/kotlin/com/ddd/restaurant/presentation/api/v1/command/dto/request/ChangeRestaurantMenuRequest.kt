package com.ddd.restaurant.presentation.api.v1.command.dto.request

import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class ChangeRestaurantMenuRequest(val newMenuItems: List<MenuItemDto>) {
    data class MenuItemDto(
            @field:NotBlank(message = "메뉴 이름은 필수 입력값입니다") val name: String,
            @field:NotNull(message = "메뉴 가격은 필수 입력값입니다") val price: BigDecimal,
            @field:NotNull(message = "메뉴 수량은 필수 입력값입니다") val quantity: Int
    )
}
