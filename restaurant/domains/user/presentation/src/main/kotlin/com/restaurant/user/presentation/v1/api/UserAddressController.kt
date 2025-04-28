package com.restaurant.user.presentation.v1.api

import com.restaurant.common.config.dto.response.CommandResultResponse
import com.restaurant.common.config.filter.CorrelationIdFilter
import com.restaurant.user.application.dto.command.DeleteAddressCommand
import com.restaurant.user.application.port.`in`.DeleteAddressUseCase
import com.restaurant.user.application.port.`in`.RegisterAddressUseCase
import com.restaurant.user.application.port.`in`.UpdateAddressUseCase
import com.restaurant.user.presentation.extensions.v1.request.toCommand
import com.restaurant.user.presentation.v1.dto.request.RegisterAddressRequestV1
import com.restaurant.user.presentation.v1.dto.request.UpdateAddressRequestV1
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import mu.KotlinLogging
import org.slf4j.MDC
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

@Tag(name = "사용자 주소 관리 API V1", description = "사용자 주소 등록, 수정, 삭제 API")
@RestController
@RequestMapping("/api/v1/users/{userId}/addresses")
class UserAddressController(
    private val registerAddressUseCase: RegisterAddressUseCase,
    private val updateAddressUseCase: UpdateAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
) {
    @Operation(summary = "주소 등록")
    @PostMapping
    fun registerAddress(
        @Parameter(description = "사용자 ID") @PathVariable userId: String,
        @Valid @RequestBody request: RegisterAddressRequestV1,
    ): ResponseEntity<CommandResultResponse> {
        log.info { "Registering address for userId: $userId" }
        val command = request.toCommand(userId)
        registerAddressUseCase.handle(command)

        val correlationId = MDC.get(CorrelationIdFilter.CORRELATION_ID_MDC_KEY) ?: "unknown"
        val response = CommandResultResponse(
            message = "주소 등록 성공",
            correlationId = correlationId,
        )

        response.add(linkTo(methodOn(UserAddressController::class.java).registerAddress(userId, request)).withSelfRel())

        log.info { "Address registered for userId: $userId" }
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "주소 수정")
    @PutMapping("/{addressId}")
    fun updateAddress(
        @Parameter(description = "사용자 ID") @PathVariable userId: String,
        @Parameter(description = "주소 ID") @PathVariable addressId: String,
        @Valid @RequestBody request: UpdateAddressRequestV1,
    ): ResponseEntity<CommandResultResponse> {
        val correlationId = MDC.get(CorrelationIdFilter.CORRELATION_ID_MDC_KEY) ?: "N/A"
        log.info { "Updating address $addressId for userId: $userId" }

        val command = request.toCommand(userId, addressId)
        updateAddressUseCase.handle(command)

        val response = CommandResultResponse(
            message = "주소 수정 성공",
            correlationId = correlationId,
        )
        response.add(linkTo(methodOn(UserAddressController::class.java).updateAddress(userId, addressId, request)).withSelfRel())

        log.info { "Address $addressId updated for userId: $userId" }
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "주소 삭제")
    @DeleteMapping("/{addressId}")
    fun deleteAddress(
        @Parameter(description = "사용자 ID") @PathVariable userId: String,
        @Parameter(description = "주소 ID") @PathVariable addressId: String,
    ): ResponseEntity<CommandResultResponse> {
        val correlationId = MDC.get(CorrelationIdFilter.CORRELATION_ID_MDC_KEY) ?: "N/A"
        log.info { "Deleting address $addressId for userId: $userId" }

        val command = DeleteAddressCommand(userId = userId, addressId = addressId)
        deleteAddressUseCase.handle(command)

        val response = CommandResultResponse(
            message = "주소 삭제 성공",
            correlationId = correlationId,
        )
        response.add(linkTo(methodOn(UserAddressController::class.java).deleteAddress(userId, addressId)).withSelfRel())

        log.info { "Address $addressId deleted for userId: $userId" }
        return ResponseEntity.ok(response)
    }
}
