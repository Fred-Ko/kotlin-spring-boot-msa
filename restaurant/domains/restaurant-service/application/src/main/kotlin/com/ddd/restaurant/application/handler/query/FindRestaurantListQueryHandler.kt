package com.ddd.restaurant.application.handler.query

import com.ddd.restaurant.application.dto.query.RestaurantListResult
import com.ddd.restaurant.application.query.FindRestaurantListQuery
import com.ddd.restaurant.domain.repository.RestaurantRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class FindRestaurantListQueryHandler(private val restaurantRepository: RestaurantRepository) :
        FindRestaurantListQuery {
        override fun findRestaurantList(page: Int, size: Int): RestaurantListResult {
                val restaurants = restaurantRepository.findAll(page, size)
                return RestaurantListResult(
                        restaurants =
                                restaurants.map {
                                        RestaurantListResult.RestaurantInfo(
                                                id = it.id,
                                                name = it.name,
                                                address =
                                                        it.address.toRestaurantListResultAddress(),
                                                status = it.status.toRestaurantListResultStatus(),
                                        )
                                }
                )
        }
}
