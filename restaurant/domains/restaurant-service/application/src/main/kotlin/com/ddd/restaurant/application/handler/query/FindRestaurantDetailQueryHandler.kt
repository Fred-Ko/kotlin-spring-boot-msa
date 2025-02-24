package com.ddd.restaurant.application.handler.query

import com.ddd.restaurant.application.dto.query.RestaurantDetailResult
import com.ddd.restaurant.application.query.FindRestaurantDetailQuery
import com.ddd.restaurant.domain.model.vo.MenuItem
import com.ddd.restaurant.domain.model.vo.RestaurantAddress
import com.ddd.restaurant.domain.model.vo.RestaurantOperationHours
import com.ddd.restaurant.domain.model.vo.RestaurantStatus
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
                        address = restaurant.address.toRestaurantDetailResultAddress(),
                        menuItems = restaurant.menuItems.toRestaurantDetailResultMenuItems(),
                        status = restaurant.status.toRestaurantDetailResultStatus(),
                        operationHours =
                                restaurant.operationHours.toRestaurantDetailResultOperationHours()
                )
        }

        private fun RestaurantAddress.toRestaurantDetailResultAddress():
                RestaurantDetailResult.Address =
                RestaurantDetailResult.Address(
                        street = this.street,
                        city = this.city,
                        zipCode = this.zipCode
                )

        private fun List<MenuItem>.toRestaurantDetailResultMenuItems():
                List<RestaurantDetailResult.MenuItem> =
                this.map {
                        RestaurantDetailResult.MenuItem(
                                name = it.name,
                                price = it.price,
                                quantity = it.quantity
                        )
                }

        private fun RestaurantStatus.toRestaurantDetailResultStatus():
                RestaurantDetailResult.RestaurantStatus =
                when (this) {
                        RestaurantStatus.OPEN -> RestaurantDetailResult.RestaurantStatus.OPEN
                        RestaurantStatus.CLOSED -> RestaurantDetailResult.RestaurantStatus.CLOSED
                }

        private fun RestaurantOperationHours.toRestaurantDetailResultOperationHours():
                RestaurantDetailResult.OperationHours =
                RestaurantDetailResult.OperationHours(
                        startTime = this.startTime,
                        endTime = this.endTime
                )
}
