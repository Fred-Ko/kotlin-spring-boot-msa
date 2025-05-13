package com.restaurant.user.presentation.v1.command.controller

import com.restaurant.user.presentation.v1.command.dto.request.RegisterAddressRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.UpdateAddressRequestV1
import com.restaurant.user.presentation.v1.command.extensions.dto.request.toCommand

import com.restaurant.common.presentation.dto.response.CommandResultResponse
import com.restaurant.user.application.dto.command.*
import com.restaurant.user.application.port.*
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.UserId

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.UUID

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/users/{userId}/addresses")
class UserAddressController(
    private val registerAddressUseCase: RegisterAddressUseCase,
    private val updateAddressUseCase: UpdateAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase
) {

    @PostMapping
    fun registerAddress(
        @PathVariable userId: UUID,
        @Valid @RequestBody request: RegisterAddressRequestV1,
        
    ): ResponseEntity<CommandResultResponse> {
        log.info { "Received request to register address for user ID: $userId" }
        val command = request.toCommand(UserId.of(userId))
        val addressId: AddressId = registerAddressUseCase.registerAddress(command)

        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{addressId}")
            .buildAndExpand(addressId.value)
            .toUri()

        return ResponseEntity.created(location).body(
            CommandResultResponse(
                status = "SUCCESS",
                message = "Address registered successfully.",
                
            )
        )
    }

    @PutMapping("/{addressId}")
    fun updateAddress(
        @PathVariable userId: UUID,
        @PathVariable addressId: UUID,
        @Valid @RequestBody request: UpdateAddressRequestV1,
        
    ): ResponseEntity<CommandResultResponse> {
        log.info { "Received request to update address ID: $addressId for user ID: $userId" }
        val command = request.toCommand(UserId.of(userId), AddressId.of(addressId))
        updateAddressUseCase.updateAddress(command)
        return ResponseEntity.ok(
            CommandResultResponse(
                status = "SUCCESS",
                message = "Address updated successfully.",
                
            )
        )
    }

    @DeleteMapping("/{addressId}")
    fun deleteAddress(
        @PathVariable userId: UUID,
        @PathVariable addressId: UUID,
        
    ): ResponseEntity<CommandResultResponse> {
        log.info { "Received request to delete address ID: $addressId for user ID: $userId" }
        val command = DeleteAddressCommand(
            userId = UserId.of(userId).value.toString(),
            addressId = AddressId.of(addressId).value.toString()
        )
        deleteAddressUseCase.deleteAddress(command)
        return ResponseEntity.ok(
            CommandResultResponse(
                status = "SUCCESS",
                message = "Address deleted successfully.",
                
            )
        )
    }
}
