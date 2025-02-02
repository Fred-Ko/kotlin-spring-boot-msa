package com.ddd.restaurant.presentation.api.v1.command

import com.ddd.restaurant.application.command.command.CreateRestaurantCommand
import com.ddd.restaurant.application.command.command.DeleteRestaurantCommand
import com.ddd.restaurant.application.command.command.UpdateRestaurantCommand
import com.ddd.restaurant.application.command.usecase.CreateRestaurantUseCase
import com.ddd.restaurant.application.command.usecase.DeleteRestaurantUseCase
import com.ddd.restaurant.application.command.usecase.UpdateRestaurantUseCase
import com.ddd.restaurant.presentation.api.v1.command.dto.request.RestaurantRequest
import com.ddd.restaurant.presentation.dto.request.RestaurantRequest
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/restaurants")
class RestaurantCommandController(
        private val createRestaurantUseCase: CreateRestaurantUseCase,
        private val updateRestaurantUseCase: UpdateRestaurantUseCase,
        private val deleteRestaurantUseCase: DeleteRestaurantUseCase
) {

        @PostMapping
        fun createRestaurant(@RequestBody request: RestaurantRequest): ResponseEntity<UUID> {
                val command =
                        CreateRestaurantCommand(
                                name = request.name,
                                address = request.address,
                                phoneNumber = request.phoneNumber
                        )
                val restaurantId = createRestaurantUseCase.execute(command).restaurantId
                return ResponseEntity.status(HttpStatus.CREATED).body(restaurantId)
        }

        @PutMapping("/{restaurantId}")
        fun updateRestaurant(
                @RequestBody request: RestaurantRequest,
                restaurantId: UUID
        ): ResponseEntity<Unit> {
                val command =
                        UpdateRestaurantCommand(
                                restaurantId = restaurantId,
                                name = request.name,
                                address = request.address,
                                phoneNumber = request.phoneNumber
                        )
                updateRestaurantUseCase.execute(command)
                return ResponseEntity.noContent().build()
        }

        @DeleteMapping("/{restaurantId}")
        fun deleteRestaurant(restaurantId: UUID): ResponseEntity<Unit> {
                val command = DeleteRestaurantCommand(restaurantId = restaurantId)
                deleteRestaurantUseCase.execute(command)
                return ResponseEntity.noContent().build()
        }
}
