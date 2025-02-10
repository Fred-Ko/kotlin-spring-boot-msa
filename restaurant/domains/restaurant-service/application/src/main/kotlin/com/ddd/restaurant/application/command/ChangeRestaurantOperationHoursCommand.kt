package com.ddd.restaurant.application.command

import com.ddd.restaurant.application.dto.command.ChangeRestaurantOperationHoursCommandDto
import com.ddd.restaurant.application.dto.result.ChangeRestaurantOperationHoursResult

interface ChangeRestaurantOperationHoursCommand {
    fun changeRestaurantOperationHours(command: ChangeRestaurantOperationHoursCommandDto): ChangeRestaurantOperationHoursResult
} 