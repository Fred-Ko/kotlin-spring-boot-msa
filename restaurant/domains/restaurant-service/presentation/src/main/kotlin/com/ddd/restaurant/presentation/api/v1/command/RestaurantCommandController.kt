package com.ddd.restaurant.presentation.api.v1.command

import com.ddd.restaurant.application.command.*
import com.ddd.restaurant.application.dto.command.*
import com.ddd.restaurant.domain.model.vo.RestaurantAddress
import com.ddd.restaurant.presentation.api.v1.command.dto.request.RestaurantRequest
import com.ddd.restaurant.presentation.api.v1.command.dto.response.RestaurantResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Restaurant Command API", description = "Restaurant Command API 입니다.")
@RestController
@RequestMapping("/api/v1/restaurants")
class RestaurantCommandController(
        private val createRestaurantUseCase: CreateRestaurantCommand,
        private val addRestaurantMenuItemUseCase: AddRestaurantMenuItemCommand,
        private val removeRestaurantMenuItemUseCase: RemoveRestaurantMenuItemCommand,
) {
    @Operation(summary = "Restaurant 생성", description = "Restaurant 를 생성합니다.")
    @PostMapping
    fun registerRestaurant(
            @Valid @RequestBody request: RestaurantRequest.CreateRestaurantRequest,
    ): ResponseEntity<RestaurantResponse.CreateRestaurantResponse> {
        val command =
                CreateRestaurantCommandDto(
                        name = request.name,
                        address = RestaurantAddress(request.address, request.city, request.zipCode),
                        phoneNumber = request.phoneNumber,
                        operatingHours = request.operatingHours,
                )
        createRestaurantUseCase.createRestaurant(command)
        return ResponseEntity.ok(
                RestaurantResponse.CreateRestaurantResponse("Restaurant created successfully")
        )
    }

    @Operation(summary = "Restaurant 메뉴 아이템 추가", description = "Restaurant 메뉴 아이템을 추가합니다.")
    @PostMapping("/{restaurantId}/menu-items")
    fun addRestaurantMenuItem(
            @PathVariable restaurantId: Long,
            @Valid @RequestBody request: RestaurantRequest.AddRestaurantMenuItemRequest,
    ): ResponseEntity<RestaurantResponse.AddRestaurantMenuItemResponse> {
        val command =
                AddRestaurantMenuItemCommandDto(
                        restaurantId = restaurantId,
                        name = request.name,
                        description = request.description,
                        price = request.price,
                        category = request.category,
                )
        addRestaurantMenuItemUseCase.addRestaurantMenuItem(command)
        return ResponseEntity.ok(
                RestaurantResponse.AddRestaurantMenuItemResponse(
                        "Restaurant menu item added successfully"
                )
        )
    }

    @Operation(summary = "Restaurant 메뉴 아이템 삭제", description = "Restaurant 메뉴 아이템을 삭제합니다.")
    @DeleteMapping("/{restaurantId}/menu-items/{menuItemId}")
    fun removeRestaurantMenuItem(
            @PathVariable restaurantId: Long,
            @PathVariable menuItemId: Long,
    ): ResponseEntity<RestaurantResponse.RemoveRestaurantMenuItemResponse> {
        val command =
                RemoveRestaurantMenuItemCommandDto(
                        restaurantId = restaurantId,
                        menuItemId = menuItemId,
                )
        removeRestaurantMenuItemUseCase.removeRestaurantMenuItem(command)
        return ResponseEntity.ok(
                RestaurantResponse.RemoveRestaurantMenuItemResponse(
                        "Restaurant menu item removed successfully"
                )
        )
    }
}
