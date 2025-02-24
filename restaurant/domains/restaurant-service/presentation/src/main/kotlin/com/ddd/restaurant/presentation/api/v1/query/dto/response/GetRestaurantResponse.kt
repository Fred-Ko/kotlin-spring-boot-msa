package com.ddd.restaurant.presentation.api.v1.query.dto.response

import com.ddd.restaurant.application.dto.result.RestaurantDetailResult
import java.util.UUID

class GetRestaurantResponse(
        val id: UUID,
        val name: String,
        val address: String,
        val phoneNumber: String,
        val operatingHours: String,
) {
    companion object {
        fun fromQueryResult(queryResult: RestaurantDetailResult): GetRestaurantResponse {
            return GetRestaurantResponse(
                    id = queryResult.id,
                    name = queryResult.name,
                    address = queryResult.address.toString(),
                    phoneNumber = "phoneNumber", // no phoneNumber in RestaurantDetailResult
                    operatingHours = queryResult.operationHours.toString(), // or format accordingly
            )
        }
    }
} 