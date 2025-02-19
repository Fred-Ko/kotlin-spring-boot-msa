package com.ddd.restaurant.presentation.api.v1.query.dto.response

import com.ddd.restaurant.application.dto.result.GetRestaurantQueryResult
import com.ddd.restaurant.application.dto.result.ListRestaurantsQueryResult

class RestaurantResponse {
    data class GetRestaurantResponse(
            val id: Long,
            val name: String,
            val address: String,
            val phoneNumber: String,
            val operatingHours: String,
    ) {
        companion object {
            fun fromQueryResult(queryResult: GetRestaurantQueryResult): GetRestaurantResponse {
                return GetRestaurantResponse(
                        id = queryResult.id,
                        name = queryResult.name,
                        address = queryResult.address,
                        phoneNumber = queryResult.phoneNumber,
                        operatingHours = queryResult.operatingHours,
                )
            }
        }
    }

    data class ListRestaurantsResponse(
            val restaurants: List<Restaurant>,
            val totalCount: Long,
            val totalPages: Int,
    ) {
        data class Restaurant(
                val id: Long,
                val name: String,
                val address: String,
        ) {
            companion object {
                fun fromQueryResultRestaurant(
                        queryResultRestaurant: ListRestaurantsQueryResult.Restaurant,
                ): Restaurant {
                    return Restaurant(
                            id = queryResultRestaurant.id,
                            name = queryResultRestaurant.name,
                            address = queryResultRestaurant.address,
                    )
                }
            }
        }

        companion object {
            fun fromQueryResult(queryResult: ListRestaurantsQueryResult): ListRestaurantsResponse {
                return ListRestaurantsResponse(
                        restaurants =
                                queryResult.restaurants.map {
                                    Restaurant.fromQueryResultRestaurant(it)
                                },
                        totalCount = queryResult.totalCount,
                        totalPages = queryResult.totalPages,
                )
            }
        }
    }
}
