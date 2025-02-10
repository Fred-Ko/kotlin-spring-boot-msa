package com.ddd.restaurant.application.handler.command

import com.ddd.restaurant.application.command.OpenRestaurantCommand
import com.ddd.restaurant.application.dto.command.OpenRestaurantCommandDto
import com.ddd.restaurant.application.dto.result.OpenRestaurantResult
import com.ddd.restaurant.domain.exception.RestaurantNotFoundException
import com.ddd.restaurant.domain.repository.RestaurantRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class OpenRestaurantCommandHandler(private val restaurantRepository: RestaurantRepository) :
        OpenRestaurantCommand {
    override fun openRestaurant(command: OpenRestaurantCommandDto): OpenRestaurantResult {
        val restaurant =
                restaurantRepository.findById(command.restaurantId)
                        ?: throw RestaurantNotFoundException(command.restaurantId.toString())
        val changedRestaurant = restaurant.openRestaurant()
        restaurantRepository.save(changedRestaurant)
        return OpenRestaurantResult(success = true)
    }
}
