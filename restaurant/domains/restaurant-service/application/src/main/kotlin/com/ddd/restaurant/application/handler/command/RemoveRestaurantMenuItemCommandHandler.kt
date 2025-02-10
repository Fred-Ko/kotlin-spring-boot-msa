package com.ddd.restaurant.application.handler.command

import com.ddd.restaurant.application.command.RemoveRestaurantMenuItemCommand
import com.ddd.restaurant.application.dto.command.RemoveRestaurantMenuItemCommandDto
import com.ddd.restaurant.application.dto.result.RemoveRestaurantMenuItemResult
import com.ddd.restaurant.domain.exception.RestaurantNotFoundException
import com.ddd.restaurant.domain.repository.RestaurantRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class RemoveRestaurantMenuItemCommandHandler(
        private val restaurantRepository: RestaurantRepository
) : RemoveRestaurantMenuItemCommand {
    override fun removeRestaurantMenuItem(
            command: RemoveRestaurantMenuItemCommandDto
    ): RemoveRestaurantMenuItemResult {
        val restaurant =
                restaurantRepository.findById(command.restaurantId)
                        ?: throw RestaurantNotFoundException(command.restaurantId.toString())
        val changedRestaurant = restaurant.removeMenuItem(command.menuItemId)
        restaurantRepository.save(changedRestaurant)
        return RemoveRestaurantMenuItemResult(success = true)
    }
}
