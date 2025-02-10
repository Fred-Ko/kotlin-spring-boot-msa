package com.ddd.restaurant.application.handler.command

import com.ddd.restaurant.application.command.AddRestaurantMenuItemCommand
import com.ddd.restaurant.application.dto.command.AddRestaurantMenuItemCommandDto
import com.ddd.restaurant.application.dto.result.AddRestaurantMenuItemResult
import com.ddd.restaurant.domain.exception.RestaurantNotFoundException
import com.ddd.restaurant.domain.repository.RestaurantRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class AddRestaurantMenuItemCommandHandler(private val restaurantRepository: RestaurantRepository) :
        AddRestaurantMenuItemCommand {
    override fun addRestaurantMenuItem(
            command: AddRestaurantMenuItemCommandDto
    ): AddRestaurantMenuItemResult {
        val restaurant =
                restaurantRepository.findById(command.restaurantId)
                        ?: throw RestaurantNotFoundException(command.restaurantId.toString())
        val changedRestaurant = restaurant.addMenuItem(command.menuItem)
        restaurantRepository.save(changedRestaurant)
        return AddRestaurantMenuItemResult(success = true)
    }
}
