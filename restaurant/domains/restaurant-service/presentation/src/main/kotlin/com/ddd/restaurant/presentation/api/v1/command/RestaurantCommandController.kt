package com.ddd.restaurant.presentation.api.v1.command

import com.ddd.restaurant.application.command.command.DeleteRestaurantCommand
import com.ddd.restaurant.application.command.result.CreateRestaurantResult
import com.ddd.restaurant.application.command.result.DeleteRestaurantResult
import com.ddd.restaurant.application.command.result.UpdateRestaurantResult
import com.ddd.restaurant.application.command.usecase.CreateRestaurantUseCase
import com.ddd.restaurant.application.command.usecase.DeleteRestaurantUseCase
import com.ddd.restaurant.application.command.usecase.UpdateRestaurantUseCase
import com.ddd.restaurant.presentation.api.v1.command.dto.request.RestaurantRequest
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
        fun createRestaurant(@RequestBody request: RestaurantRequest): ResponseEntity<Any> {
                val command = request.toCreateCommand()
                return when (  val result = createRestaurantUseCase.execute(command)) {
                        is CreateRestaurantResult.Success ->
                                ResponseEntity.status(HttpStatus.CREATED).body(result.restaurantId)
                        is CreateRestaurantResult.Failure.InvalidInput ->
                                ResponseEntity.badRequest().body(result.message)
                        is CreateRestaurantResult.Failure.RestaurantCreationFailed ->
                                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(result.message)
                        is CreateRestaurantResult.Failure.ValidationError ->
                                ResponseEntity.badRequest().body(result.message)
                }
        }

        @PutMapping("/{restaurantId}")
        fun updateRestaurant(
                @RequestBody request: RestaurantRequest,
                @PathVariable restaurantId: UUID
        ): ResponseEntity<Any> {
                val command = request.toUpdateCommand(restaurantId.toString())
                return when ( val result = updateRestaurantUseCase.execute(command)) {
                        is UpdateRestaurantResult.Success -> ResponseEntity.noContent().build()
                        is UpdateRestaurantResult.Failure.RestaurantNotFound ->
                                ResponseEntity.notFound().build()
                        is UpdateRestaurantResult.Failure.UpdateFailed ->
                                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(result.message)
                        is UpdateRestaurantResult.Failure.ValidationError ->
                                ResponseEntity.badRequest().body(result.message)
                }
        }

        @DeleteMapping("/{restaurantId}")
        fun deleteRestaurant(@PathVariable restaurantId: UUID): ResponseEntity<Any> {
                val command = DeleteRestaurantCommand(restaurantId = restaurantId.toString())
                return when (val result = deleteRestaurantUseCase.execute(command)) {
                        is DeleteRestaurantResult.Success -> ResponseEntity.noContent().build()
                        is DeleteRestaurantResult.Failure.RestaurantNotFound ->
                                ResponseEntity.notFound().build()
                        is DeleteRestaurantResult.Failure.DeleteFailed ->
                                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(result.message)
                        is DeleteRestaurantResult.Failure.ValidationError ->
                                ResponseEntity.badRequest().body(result.message)
                }
        }
}
