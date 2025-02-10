package com.ddd.restaurant.application.handler.command

import com.ddd.restaurant.application.command.ChangeRestaurantOperationHoursCommand
import com.ddd.restaurant.application.dto.command.ChangeRestaurantOperationHoursCommandDto
import com.ddd.restaurant.application.dto.result.ChangeRestaurantOperationHoursResult
import com.ddd.restaurant.domain.exception.RestaurantNotFoundException
import com.ddd.restaurant.domain.repository.RestaurantRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class ChangeRestaurantOperationHoursCommandHandler(
        private val restaurantRepository: RestaurantRepository
) : ChangeRestaurantOperationHoursCommand {
    override fun changeRestaurantOperationHours(
            command: ChangeRestaurantOperationHoursCommandDto
    ): ChangeRestaurantOperationHoursResult {
        val restaurant =
                restaurantRepository.findById(command.restaurantId)
                        ?: throw RestaurantNotFoundException(command.restaurantId.toString())
        val changedRestaurant = restaurant.changeOperationHours(command.newOperationHours)
        restaurantRepository.save(changedRestaurant)
        return ChangeRestaurantOperationHoursResult(success = true)
    }
}
