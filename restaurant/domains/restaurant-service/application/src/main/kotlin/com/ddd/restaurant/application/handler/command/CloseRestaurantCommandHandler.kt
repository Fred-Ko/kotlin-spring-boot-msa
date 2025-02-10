package com.ddd.restaurant.application.handler.command

import com.ddd.restaurant.application.command.CloseRestaurantCommand
import com.ddd.restaurant.application.dto.command.CloseRestaurantCommandDto
import com.ddd.restaurant.application.dto.result.CloseRestaurantResult
import com.ddd.restaurant.domain.exception.RestaurantNotFoundException
import com.ddd.restaurant.domain.repository.RestaurantRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class CloseRestaurantCommandHandler(private val restaurantRepository: RestaurantRepository) :
        CloseRestaurantCommand {
    override fun closeRestaurant(command: CloseRestaurantCommandDto): CloseRestaurantResult {
        val restaurant =
                restaurantRepository.findById(command.restaurantId)
                        ?: throw RestaurantNotFoundException(command.restaurantId.toString())
        val changedRestaurant = restaurant.closeRestaurant()
        restaurantRepository.save(changedRestaurant)
        return CloseRestaurantResult(success = true)
    }
}
