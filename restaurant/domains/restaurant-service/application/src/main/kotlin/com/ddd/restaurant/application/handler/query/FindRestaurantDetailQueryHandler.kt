package com.ddd.restaurant.application.handler.query

import com.ddd.restaurant.application.dto.query.RestaurantDetailResult
import com.ddd.restaurant.application.query.FindRestaurantDetailQuery
import com.ddd.restaurant.domain.repository.RestaurantRepository
import java.util.UUID
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class FindRestaurantDetailQueryHandler(private val restaurantRepository: RestaurantRepository) :
        FindRestaurantDetailQuery {
    override fun findRestaurantDetail(restaurantId: UUID): RestaurantDetailResult {
        val restaurant =
                restaurantRepository.findById(restaurantId)
                        ?: throw NoSuchElementException("Restaurant not found")

        return RestaurantDetailResult(
                id = restaurant.id,
                name = restaurant.name,
                address = restaurant.address,
                menuItems = restaurant.menuItems,
                status = restaurant.status,
                operationHours = restaurant.operationHours
        )
    }
}
