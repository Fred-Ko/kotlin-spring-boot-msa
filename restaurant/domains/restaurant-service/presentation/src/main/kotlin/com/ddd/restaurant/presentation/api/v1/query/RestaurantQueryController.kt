package com.ddd.restaurant.presentation.api.v1.query

import com.ddd.restaurant.application.dto.result.RestaurantDetailResult
import com.ddd.restaurant.application.dto.result.RestaurantListResult
import com.ddd.restaurant.application.query.FindRestaurantDetailQuery
import com.ddd.restaurant.application.query.FindRestaurantListQuery
import com.ddd.restaurant.presentation.api.v1.query.dto.request.RestaurantDetailRequest
import com.ddd.restaurant.presentation.api.v1.query.dto.request.RestaurantListRequest
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/restaurants")
class RestaurantQueryController(
        private val findRestaurantListQuery: FindRestaurantListQuery,
        private val findRestaurantDetailQuery: FindRestaurantDetailQuery
) {

    @GetMapping
    fun getRestaurantList(request: RestaurantListRequest): ResponseEntity<RestaurantListResult> {
        val result =
                findRestaurantListQuery.findRestaurantList(page = request.page, size = request.size)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/{restaurantId}")
    fun getRestaurantDetail(
            @PathVariable restaurantId: UUID
    ): ResponseEntity<RestaurantDetailResult> {
        val request = RestaurantDetailRequest(restaurantId = restaurantId)
        val result =
                findRestaurantDetailQuery.findRestaurantDetail(restaurantId = request.restaurantId)
        return ResponseEntity.ok(result)
    }
}
