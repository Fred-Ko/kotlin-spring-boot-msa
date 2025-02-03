package com.ddd.restaurant.presentation.api.v1.query

import com.ddd.restaurant.application.query.query.GetRestaurantQuery
import com.ddd.restaurant.application.query.query.GetRestaurantsQuery
import com.ddd.restaurant.application.query.result.GetRestaurantResult
import com.ddd.restaurant.application.query.result.GetRestaurantsResult
import com.ddd.restaurant.application.query.usecase.GetRestaurantUseCase
import com.ddd.restaurant.application.query.usecase.GetRestaurantsUseCase
import com.ddd.restaurant.presentation.api.v1.query.dto.response.RestaurantResponse
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/restaurants")
class RestaurantQueryController(
        private val getRestaurantUseCase: GetRestaurantUseCase,
        private val getRestaurantsUseCase: GetRestaurantsUseCase
) {

    @GetMapping("/{restaurantId}")
    fun getRestaurant(@PathVariable restaurantId: UUID): ResponseEntity<RestaurantResponse> {
        val query = GetRestaurantQuery(restaurantId = restaurantId)
        return when (val result = getRestaurantUseCase.execute(query)) {
            is GetRestaurantResult.Success ->
                    ResponseEntity.ok(RestaurantResponse.from(result.restaurant))
            is GetRestaurantResult.Failure.RestaurantNotFound -> ResponseEntity.notFound().build()
            is GetRestaurantResult.Failure.ValidationError -> ResponseEntity.badRequest().build()
        }
    }

    @GetMapping
    fun getRestaurants(
            @PageableDefault(page = 0, size = 10) pageable: Pageable
    ): ResponseEntity<Page<RestaurantResponse>> {
        val query = GetRestaurantsQuery(page = pageable.pageNumber, size = pageable.pageSize)
        return when ( val result = getRestaurantsUseCase.execute(query)) {
            is GetRestaurantsResult.Success ->
                    ResponseEntity.ok(result.restaurantsPage.map { RestaurantResponse.from(it) })
            is GetRestaurantsResult.Failure.ValidationError -> ResponseEntity.badRequest().build()
        }
    }
}
