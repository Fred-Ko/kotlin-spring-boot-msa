package com.ddd.restaurant.presentation.api.v1.query

import com.ddd.restaurant.application.query.GetRestaurantQuery
import com.ddd.restaurant.application.query.ListRestaurantsQuery
import com.ddd.restaurant.presentation.api.v1.query.dto.response.RestaurantResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "Restaurant Query API", description = "Restaurant Query API 입니다.")
@Validated
@RestController
@RequestMapping("/api/v1/restaurants")
class RestaurantQueryController(
        private val getRestaurantQueryUseCase: GetRestaurantQuery,
        private val listRestaurantsQueryUseCase: ListRestaurantsQuery,
) {
    @Operation(summary = "Restaurant 조회", description = "ID 로 Restaurant 를 조회합니다.")
    @GetMapping("/{id}")
    fun getRestaurant(
            @PathVariable id: Long
    ): ResponseEntity<RestaurantResponse.GetRestaurantResponse> {
        val queryResult = getRestaurantQueryUseCase.getRestaurant(id)
        val response = RestaurantResponse.GetRestaurantResponse.fromQueryResult(queryResult)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Restaurant 목록 조회", description = "Restaurant 목록을 페이지네이션으로 조회합니다.")
    @GetMapping
    fun listRestaurants(
            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(defaultValue = "0")
            @Min(0)
            page: Int,
            @Parameter(description = "페이지 크기 (최대 100)")
            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(100)
            size: Int,
    ): ResponseEntity<RestaurantResponse.ListRestaurantsResponse> {
        val queryResult = listRestaurantsQueryUseCase.listRestaurants(page, size)
        val response = RestaurantResponse.ListRestaurantsResponse.fromQueryResult(queryResult)
        return ResponseEntity.ok(response)
    }
}
