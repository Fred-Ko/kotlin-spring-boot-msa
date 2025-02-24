package com.ddd.restaurant.presentation.api.v1.command.dto.request

import jakarta.validation.constraints.NotNull
import java.time.LocalTime

data class ChangeRestaurantOperationHoursRequest(
        @field:NotNull(message = "영업 시간은 필수 입력값입니다")
        val newOperationHours: RestaurantOperationHoursDto
) {
        data class RestaurantOperationHoursDto(
                @field:NotNull(message = "영업 시작 시간은 필수 입력값입니다") val startTime: LocalTime,
                @field:NotNull(message = "영업 종료 시간은 필수 입력값입니다") val endTime: LocalTime
        )
}
