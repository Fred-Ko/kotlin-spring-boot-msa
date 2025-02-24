package com.ddd.restaurant.presentation.api.v1.command.dto.request

import jakarta.validation.constraints.NotBlank

data class RemoveRestaurantMenuItemRequest(
        @field:NotBlank(message = "메뉴 아이템 ID는 필수 입력값입니다") val menuItemId: String
)
