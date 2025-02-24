package com.ddd.restaurant.presentation.api.v1.query.dto.response

import com.ddd.restaurant.application.dto.result.RestaurantListResult
import java.util.UUID

class ListRestaurantsResponse(
        val restaurants: List<Restaurant>,
        val totalCount: Long,
        val totalPages: Int,
) {
    data class Restaurant(
            val id: UUID,
            val name: String,
            val address: String,
    ) {
        companion object {
            fun fromQueryResultRestaurant(
                    queryResultRestaurant: RestaurantListResult.RestaurantInfo,
            ): Restaurant {
                return Restaurant(
                        id = queryResultRestaurant.id,
                        name = queryResultRestaurant.name,
                        address = queryResultRestaurant.address.toString(), // or map fields
                )
            }
        }
    }

    companion object {
        fun fromQueryResult(queryResult: RestaurantListResult): ListRestaurantsResponse {
            return ListRestaurantsResponse(
                    restaurants =
                            queryResult.restaurants.map {
                                Restaurant.fromQueryResultRestaurant(it)
                            },
                    totalCount = queryResult.restaurants.size.toLong(), // no totalCount in RestaurantListResult
                    totalPages = 1, // no totalPages in RestaurantListResult
            )
        }
    }
} 