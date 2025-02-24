package com.ddd.restaurant.presentation.api.v1.command

import com.ddd.restaurant.application.command.AddRestaurantMenuItemCommand
import com.ddd.restaurant.application.command.ChangeRestaurantInfoCommand
import com.ddd.restaurant.application.command.ChangeRestaurantMenuCommand
import com.ddd.restaurant.application.command.ChangeRestaurantOperationHoursCommand
import com.ddd.restaurant.application.command.CloseRestaurantCommand
import com.ddd.restaurant.application.command.CreateRestaurantCommand
import com.ddd.restaurant.application.command.OpenRestaurantCommand
import com.ddd.restaurant.application.command.RemoveRestaurantMenuItemCommand
import com.ddd.restaurant.application.dto.command.AddRestaurantMenuItemCommandDto
import com.ddd.restaurant.application.dto.command.ChangeRestaurantInfoCommandDto
import com.ddd.restaurant.application.dto.command.ChangeRestaurantMenuCommandDto
import com.ddd.restaurant.application.dto.command.ChangeRestaurantOperationHoursCommandDto
import com.ddd.restaurant.application.dto.command.CloseRestaurantCommandDto
import com.ddd.restaurant.application.dto.command.CreateRestaurantCommandDto
import com.ddd.restaurant.application.dto.command.OpenRestaurantCommandDto
import com.ddd.restaurant.application.dto.command.RemoveRestaurantMenuItemCommandDto
import com.ddd.restaurant.presentation.api.v1.command.dto.request.AddRestaurantMenuItemRequest
import com.ddd.restaurant.presentation.api.v1.command.dto.request.ChangeRestaurantInfoRequest
import com.ddd.restaurant.presentation.api.v1.command.dto.request.ChangeRestaurantMenuRequest
import com.ddd.restaurant.presentation.api.v1.command.dto.request.ChangeRestaurantOperationHoursRequest
import com.ddd.restaurant.presentation.api.v1.command.dto.request.CloseRestaurantRequest
import com.ddd.restaurant.presentation.api.v1.command.dto.request.CreateRestaurantRequest
import com.ddd.restaurant.presentation.api.v1.command.dto.request.OpenRestaurantRequest
import jakarta.validation.Valid
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/restaurants")
class RestaurantCommandController(
        private val createRestaurantCommand: CreateRestaurantCommand,
        private val changeRestaurantInfoCommand: ChangeRestaurantInfoCommand,
        private val changeRestaurantMenuCommand: ChangeRestaurantMenuCommand,
        private val changeRestaurantOperationHoursCommand: ChangeRestaurantOperationHoursCommand,
        private val addRestaurantMenuItemCommand: AddRestaurantMenuItemCommand,
        private val removeRestaurantMenuItemCommand: RemoveRestaurantMenuItemCommand,
        private val openRestaurantCommand: OpenRestaurantCommand,
        private val closeRestaurantCommand: CloseRestaurantCommand
) {
        @PostMapping
        fun createRestaurant(
                @RequestBody @Valid request: CreateRestaurantRequest
        ): ResponseEntity<Void> {
                createRestaurantCommand.createRestaurant(
                        CreateRestaurantCommandDto(
                                name = request.name,
                                address =
                                        request.address.let {
                                                CreateRestaurantCommandDto.RestaurantAddressDto(
                                                        it.street,
                                                        it.city,
                                                        it.zipCode
                                                )
                                        },
                                menuItems =
                                        request.menuItems.map { menuItemRequest ->
                                                CreateRestaurantCommandDto.MenuItemDto(
                                                        menuItemRequest.name,
                                                        menuItemRequest.price,
                                                        menuItemRequest.quantity
                                                )
                                        },
                                operationHours =
                                        request.operationHours.let {
                                                CreateRestaurantCommandDto
                                                        .RestaurantOperationHoursDto(
                                                                it.startTime,
                                                                it.endTime
                                                        )
                                        }
                        )
                )
                return ResponseEntity.status(HttpStatus.CREATED).build()
        }

        @PatchMapping("/{restaurantId}")
        fun changeRestaurantInfo(
                @PathVariable restaurantId: UUID,
                @RequestBody @Valid request: ChangeRestaurantInfoRequest
        ): ResponseEntity<Void> {
                changeRestaurantInfoCommand.changeRestaurantInfo(
                        ChangeRestaurantInfoCommandDto(
                                restaurantId = restaurantId,
                                newName = request.newName,
                                newAddress =
                                        request.newAddress?.let { addressRequest ->
                                                ChangeRestaurantInfoCommandDto.RestaurantAddressDto(
                                                        addressRequest.street,
                                                        addressRequest.city,
                                                        addressRequest.zipCode
                                                )
                                        }
                        )
                )
                return ResponseEntity.ok().build()
        }

        @PatchMapping("/{restaurantId}/menu")
        fun changeRestaurantMenu(
                @PathVariable restaurantId: UUID,
                @RequestBody @Valid request: ChangeRestaurantMenuRequest
        ): ResponseEntity<Void> {
                changeRestaurantMenuCommand.changeRestaurantMenu(
                        ChangeRestaurantMenuCommandDto(
                                restaurantId = restaurantId,
                                newMenuItems =
                                        request.newMenuItems.map { menuItemRequest ->
                                                ChangeRestaurantMenuCommandDto.MenuItemDto(
                                                        menuItemRequest.name,
                                                        menuItemRequest.price,
                                                        menuItemRequest.quantity
                                                )
                                        }
                        )
                )
                return ResponseEntity.ok().build()
        }

        @PatchMapping("/{restaurantId}/operation-hours")
        fun changeRestaurantOperationHours(
                @PathVariable restaurantId: UUID,
                @RequestBody @Valid request: ChangeRestaurantOperationHoursRequest
        ): ResponseEntity<Void> {
                changeRestaurantOperationHoursCommand.changeRestaurantOperationHours(
                        ChangeRestaurantOperationHoursCommandDto(
                                restaurantId = restaurantId,
                                newOperationHours =
                                        request.newOperationHours.let { operationHoursRequest ->
                                                ChangeRestaurantOperationHoursCommandDto
                                                        .RestaurantOperationHoursDto(
                                                                operationHoursRequest.startTime,
                                                                operationHoursRequest.endTime
                                                        )
                                        }
                        )
                )
                return ResponseEntity.ok().build()
        }

        @PostMapping("/{restaurantId}/menu-items")
        fun addRestaurantMenuItem(
                @PathVariable restaurantId: UUID,
                @RequestBody @Valid request: AddRestaurantMenuItemRequest
        ): ResponseEntity<Void> {
                addRestaurantMenuItemCommand.addRestaurantMenuItem(
                        AddRestaurantMenuItemCommandDto(
                                restaurantId = restaurantId,
                                menuItem =
                                        request.menuItem.let { menuItemRequest ->
                                                AddRestaurantMenuItemCommandDto.MenuItemDto(
                                                        menuItemRequest.name,
                                                        menuItemRequest.price,
                                                        menuItemRequest.quantity
                                                )
                                        }
                        )
                )
                return ResponseEntity.status(HttpStatus.CREATED).build()
        }

        @DeleteMapping("/{restaurantId}/menu-items/{menuItemId}")
        fun removeRestaurantMenuItem(
                @PathVariable restaurantId: UUID,
                @PathVariable menuItemId: String,
        ): ResponseEntity<Void> {
                removeRestaurantMenuItemCommand.removeRestaurantMenuItem(
                        RemoveRestaurantMenuItemCommandDto(
                                restaurantId = restaurantId,
                                menuItemId = menuItemId
                        )
                )
                return ResponseEntity.ok().build()
        }

        @PostMapping("/{restaurantId}/open")
        fun openRestaurant(
                @RequestBody @Valid request: OpenRestaurantRequest
        ): ResponseEntity<Void> {
                openRestaurantCommand.openRestaurant(
                        OpenRestaurantCommandDto(restaurantId = request.restaurantId)
                )
                return ResponseEntity.ok().build()
        }

        @PostMapping("/{restaurantId}/close")
        fun closeRestaurant(
                @RequestBody @Valid request: CloseRestaurantRequest
        ): ResponseEntity<Void> {
                closeRestaurantCommand.closeRestaurant(
                        CloseRestaurantCommandDto(restaurantId = request.restaurantId)
                )
                return ResponseEntity.ok().build()
        }
}
