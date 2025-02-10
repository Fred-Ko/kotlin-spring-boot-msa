package com.ddd.restaurant.application.handler.command

import com.ddd.restaurant.application.command.ChangeRestaurantMenuCommand
import com.ddd.restaurant.application.dto.command.ChangeRestaurantMenuCommandDto
import com.ddd.restaurant.application.dto.result.ChangeRestaurantMenuResult
import com.ddd.restaurant.domain.exception.RestaurantNotFoundException
import com.ddd.restaurant.domain.repository.RestaurantRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class ChangeRestaurantMenuCommandHandler(private val restaurantRepository: RestaurantRepository) :
        ChangeRestaurantMenuCommand {
    override fun changeRestaurantMenu(
            command: ChangeRestaurantMenuCommandDto
    ): ChangeRestaurantMenuResult {
        val restaurant =
                restaurantRepository.findById(command.restaurantId)
                        ?: throw RestaurantNotFoundException(command.restaurantId.toString())
        val changedRestaurant = restaurant.changeMenu(command.newMenuItems)
        restaurantRepository.save(changedRestaurant)
        return ChangeRestaurantMenuResult(success = true)
    }
}
