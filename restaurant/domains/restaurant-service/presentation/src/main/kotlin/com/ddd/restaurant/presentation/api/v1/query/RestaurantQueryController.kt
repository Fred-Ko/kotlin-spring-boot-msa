package com.ddd.restaurant.presentation.api.v1.query

import com.ddd.restaurant.application.query.query.GetRestaurantQuery
import com.ddd.restaurant.application.query.query.GetRestaurantsQuery
import com.ddd.restaurant.application.query.usecase.GetRestaurantUseCase
import com.ddd.restaurant.application.query.usecase.GetRestaurantsUseCase
import com.ddd.restaurant.presentation.dto.response.RestaurantResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/restaurants")
class RestaurantQueryController(
    private val getRestaurantUseCase: GetRestaurantUseCase,
    private val getRestaurantsUseCase: GetRestaurantsUseCase
) {

    @GetMapping("/{restaurantId}")
    fun getRestaurant(@PathVariable restaurantId: UUID): ResponseEntity<RestaurantResponse> {
        val query = GetRestaurantQuery(restaurantId = restaurantId)
        val result = getRestaurantUseCase.execute(query)
        return when (result) {
            is GetRestaurantResult.Success -> ResponseEntity.ok(RestaurantResponse.from(result.restaurant))
            is GetRestaurantResult.Failure.RestaurantNotFound -> ResponseEntity.notFound().build()
            is GetRestaurantResult.Failure.ValidationError -> ResponseEntity.badRequest().body(RestaurantResponse.ErrorResponse(result.message))
        }
    }

    @GetMapping
    fun getRestaurants(@PageableDefault page = 0, @PageableDefault size = 10): ResponseEntity<Page<RestaurantResponse>> {
        val query = GetRestaurantsQuery(page = page, size = size)
        val result = getRestaurantsUseCase.execute(query)
        return when (result) {
            is GetRestaurantsResult.Success -> ResponseEntity.ok(result.restaurantsPage.map { RestaurantResponse.from(it) })
            is GetRestaurantsResult.Failure.ValidationError -> ResponseEntity.badRequest().body(Page.empty<RestaurantResponse>()) // Error handling 필요
        }
    }
}
