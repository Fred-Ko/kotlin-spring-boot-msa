package com.ddd.restaurant.application.query.handler

import com.ddd.restaurant.application.query.dto.RestaurantDto
import com.ddd.restaurant.application.query.query.GetRestaurantQuery
import com.ddd.restaurant.application.query.result.GetRestaurantResult
import com.ddd.restaurant.application.query.usecase.GetRestaurantUseCase
import com.ddd.restaurant.domain.port.repository.RestaurantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetRestaurantUseCaseImpl(private val restaurantRepository: RestaurantRepository) :
    GetRestaurantUseCase {

    @Transactional(readOnly = true)
    override fun execute(query: GetRestaurantQuery): GetRestaurantResult {
        return try {
            val restaurant =
                restaurantRepository.findById(query.restaurantId)
                    ?: return GetRestaurantResult.Failure.RestaurantNotFound(query.restaurantId.toString())

            GetRestaurantResult.Success(RestaurantDto.from(restaurant))
        } catch (e: Exception) {
            GetRestaurantResult.Failure.ValidationError(e.message ?: "Unknown error")
        }
    }
}
