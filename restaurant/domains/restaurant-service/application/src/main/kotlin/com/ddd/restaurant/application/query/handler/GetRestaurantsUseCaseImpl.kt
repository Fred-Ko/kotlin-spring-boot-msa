package com.ddd.restaurant.application.query.handler

import com.ddd.restaurant.application.query.dto.RestaurantDto
import com.ddd.restaurant.application.query.query.GetRestaurantsQuery
import com.ddd.restaurant.application.query.result.GetRestaurantsResult
import com.ddd.restaurant.application.query.usecase.GetRestaurantsUseCase
import com.ddd.restaurant.domain.model.aggregate.Restaurant
import com.ddd.restaurant.domain.port.repository.RestaurantRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetRestaurantsUseCaseImpl(private val restaurantRepository: RestaurantRepository) :
        GetRestaurantsUseCase {

    @Transactional(readOnly = true)
    override fun execute(query: GetRestaurantsQuery): GetRestaurantsResult {
        val pageRequest = PageRequest.of(query.page, query.size)
        val restaurantsPage: Page<Restaurant> = restaurantRepository.findAll(pageRequest)
        return try {
            GetRestaurantsResult.Success(restaurantsPage.map { RestaurantDto.from(it) })
        } catch (e: Exception) {
            GetRestaurantsResult.Failure.ValidationError(e.message ?: "Unknown error")
        }
    }
}
