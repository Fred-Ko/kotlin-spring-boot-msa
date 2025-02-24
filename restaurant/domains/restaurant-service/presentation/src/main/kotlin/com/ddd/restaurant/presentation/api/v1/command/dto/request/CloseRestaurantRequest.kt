package com.ddd.restaurant.presentation.api.v1.command.dto.request

import jakarta.validation.constraints.NotNull
import java.util.UUID

data class CloseRestaurantRequest(
        @field:NotNull(message = "레스토랑 ID는 필수 입력값입니다") val restaurantId: UUID
)
