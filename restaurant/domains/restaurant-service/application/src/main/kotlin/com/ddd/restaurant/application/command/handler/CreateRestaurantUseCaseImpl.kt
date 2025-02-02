package com.ddd.restaurant.application.command.handler

import com.ddd.restaurant.application.command.command.CreateRestaurantCommand
import com.ddd.restaurant.application.command.result.CreateRestaurantResult
import com.ddd.restaurant.application.command.usecase.CreateRestaurantUseCase
import com.ddd.restaurant.domain.model.aggregate.Restaurant
import com.ddd.restaurant.domain.model.vo.Location
import com.ddd.restaurant.domain.model.vo.OperatingHours
import com.ddd.restaurant.domain.model.vo.RestaurantName
import com.ddd.restaurant.domain.port.repository.RestaurantRepository
import java.time.LocalTime
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateRestaurantUseCaseImpl(private val restaurantRepository: RestaurantRepository) :
        CreateRestaurantUseCase {

    @Transactional
    override fun execute(command: CreateRestaurantCommand): CreateRestaurantResult {
        return try {
            if (command.name.isBlank()) {
                return CreateRestaurantResult.Failure.InvalidInput(
                        "Restaurant name cannot be blank"
                )
            }

            val restaurant: Restaurant =
                    Restaurant.create(
                            name = RestaurantName(command.name),
                            operatingHours =
                                    OperatingHours(
                                            startTime =
                                                    LocalTime.parse(
                                                            command.operatingHours.openTime
                                                    ),
                                            endTime =
                                                    LocalTime.parse(
                                                            command.operatingHours.closeTime
                                                    )
                                    ),
                            location =
                                    Location(
                                            latitude = command.location.latitude,
                                            longitude = command.location.longitude
                                    ),
                    )
            restaurantRepository.save(restaurant)
            CreateRestaurantResult.Success(restaurant.id)
        } catch (e: Exception) {
            CreateRestaurantResult.Failure.RestaurantCreationFailed(
                    e.message ?: "Failed to create restaurant"
            )
        }
    }
}
