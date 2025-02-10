package com.ddd.restaurant.application.handler.command

import com.ddd.restaurant.application.command.ChangeRestaurantInfoCommand
import com.ddd.restaurant.application.dto.command.ChangeRestaurantInfoCommandDto
import com.ddd.restaurant.application.dto.result.ChangeRestaurantInfoResult
import com.ddd.restaurant.domain.exception.RestaurantNotFoundException
import com.ddd.restaurant.domain.repository.RestaurantRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class ChangeRestaurantInfoCommandHandler(private val restaurantRepository: RestaurantRepository) :
        ChangeRestaurantInfoCommand {
    override fun changeRestaurantInfo(
            command: ChangeRestaurantInfoCommandDto
    ): ChangeRestaurantInfoResult {
        val restaurant =
                restaurantRepository.findById(command.restaurantId)
                        ?: throw RestaurantNotFoundException(command.restaurantId.toString())
        val changedRestaurant =
                restaurant.changeRestaurantInfo(
                        newName = command.newName,
                        newAddress = command.newAddress
                )
        restaurantRepository.save(changedRestaurant)
        return ChangeRestaurantInfoResult(success = true)
    }
}
