package com.restaurant.user.presentation.v1.api

import com.restaurant.common.presentation.dto.response.CommandResultResponse
import com.restaurant.common.presentation.filter.CorrelationIdFilter
import com.restaurant.user.application.usecase.RegisterAddressUseCase
import com.restaurant.user.application.usecase.UpdateAddressUseCase
import com.restaurant.user.application.usecase.DeleteAddressUseCase
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.presentation.v1.dto.request.RegisterAddressRequestV1
import com.restaurant.user.presentation.v1.dto.request.UpdateAddressRequestV1
import com.restaurant.user.presentation.v1.extensions.command.dto.request.toCommand
import org.slf4j.MDC
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users/{userId}/addresses")
class UserAddressController(
    private val registerAddressUseCase: RegisterAddressUseCase,
    private val updateAddressUseCase: UpdateAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase
) {

    @PostMapping
    fun registerAddress(
        @PathVariable userId: String,
        @RequestBody request: RegisterAddressRequestV1
    ): ResponseEntity<CommandResultResponse> {
        val command = request.toCommand(UserId.of(userId))
        val registeredAddressId = registerAddressUseCase.registerAddress(command)

        val correlationId = MDC.get(CorrelationIdFilter.CORRELATION_ID_MDC_KEY) ?: "unknown"
        val responseDto = CommandResultResponse(
            message = "주소 등록 성공 (ID: ${registeredAddressId.value})",
            correlationId = correlationId
        )
        responseDto.add(linkTo(methodOn(UserAddressController::class.java).registerAddress(userId, request)).withSelfRel())

        return ResponseEntity.ok(responseDto)
    }

    @PutMapping("/{addressId}")
    fun updateAddress(
        @PathVariable userId: String,
        @PathVariable addressId: String,
        @RequestBody request: UpdateAddressRequestV1
    ): ResponseEntity<CommandResultResponse> {
        val command = request.toCommand(UserId.of(userId), AddressId.of(addressId))
        updateAddressUseCase.updateAddress(command)

        val correlationId = MDC.get(CorrelationIdFilter.CORRELATION_ID_MDC_KEY) ?: "unknown"
        val responseDto = CommandResultResponse(
            message = "주소 수정 성공 (ID: $addressId)",
            correlationId = correlationId
        )
        responseDto.add(linkTo(methodOn(UserAddressController::class.java).updateAddress(userId, addressId, request)).withSelfRel())

        return ResponseEntity.ok(responseDto)
    }

    @DeleteMapping("/{addressId}")
    fun deleteAddress(
        @PathVariable userId: String,
        @PathVariable addressId: String
    ): ResponseEntity<CommandResultResponse> {
        deleteAddressUseCase.deleteAddress(UserId.of(userId), AddressId.of(addressId))

        val correlationId = MDC.get(CorrelationIdFilter.CORRELATION_ID_MDC_KEY) ?: "unknown"
        val responseDto = CommandResultResponse(
            message = "주소 삭제 성공 (ID: $addressId)",
            correlationId = correlationId
        )
        responseDto.add(linkTo(methodOn(UserAddressController::class.java).deleteAddress(userId, addressId)).withSelfRel())

        return ResponseEntity.ok(responseDto)
    }
}
