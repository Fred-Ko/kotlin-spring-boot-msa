package com.ddd.restaurant.application.handler.command

import com.ddd.restaurant.application.command.CreateRestaurantCommand
import com.ddd.restaurant.application.dto.command.CreateRestaurantCommandDto
import com.ddd.restaurant.application.dto.result.CreateRestaurantResult
import com.ddd.restaurant.domain.model.aggregate.Restaurant
import com.ddd.restaurant.domain.repository.RestaurantRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class CreateRestaurantCommandHandler(private val restaurantRepository: RestaurantRepository) :
        CreateRestaurantCommand {
        override fun createRestaurant(command: CreateRestaurantCommandDto): CreateRestaurantResult {
                val restaurant =
                        Restaurant.create(
                                name = command.name,
                                address = command.address,
                                menuItems = command.menuItems,
                                operationHours = command.operationHours
                        )
                val savedRestaurant = restaurantRepository.save(restaurant)
                return CreateRestaurantResult(restaurantId = savedRestaurant.id)
        }
}
