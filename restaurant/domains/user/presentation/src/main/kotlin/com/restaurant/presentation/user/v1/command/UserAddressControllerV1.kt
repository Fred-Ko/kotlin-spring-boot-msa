package com.restaurant.presentation.user.v1.command

import com.restaurant.application.user.command.DeleteAddressCommand
import com.restaurant.application.user.command.handler.DeleteAddressCommandHandler
import com.restaurant.application.user.command.handler.RegisterAddressCommandHandler
import com.restaurant.application.user.command.handler.UpdateAddressCommandHandler
import com.restaurant.common.presentation.dto.response.CommandResultResponse
import com.restaurant.presentation.user.extensions.v1.request.toCommand
import com.restaurant.presentation.user.v1.command.dto.request.RegisterAddressRequestV1
import com.restaurant.presentation.user.v1.command.dto.request.UpdateAddressRequestV1
import com.restaurant.presentation.user.v1.query.UserQueryControllerV1
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/users/{userId}/addresses")
@Tag(name = "주소 관리", description = "사용자의 배달 주소 등록, 수정, 삭제 API")
class UserAddressControllerV1(
    private val registerAddressCommandHandler: RegisterAddressCommandHandler,
    private val updateAddressCommandHandler: UpdateAddressCommandHandler,
    private val deleteAddressCommandHandler: DeleteAddressCommandHandler,
) {
    private val log = LoggerFactory.getLogger(UserAddressControllerV1::class.java)

    private fun getOrGenerateCorrelationId(headerValue: String?): String =
        if (!headerValue.isNullOrBlank()) headerValue else UUID.randomUUID().toString()

    @PostMapping
    @Operation(summary = "주소 등록", description = "사용자의 새로운 배달 주소를 등록합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "주소 등록 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = CommandResultResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 데이터",
                content = [Content(mediaType = "application/problem+json")],
            ),
            ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음",
                content = [Content(mediaType = "application/problem+json")],
            ),
        ],
    )
    fun registerAddress(
        @Parameter(hidden = true) @RequestHeader("X-Correlation-Id", required = false) correlationIdHeader: String?,
        @Parameter(description = "사용자 ID", required = true) @PathVariable userId: String,
        @Valid @RequestBody request: RegisterAddressRequestV1,
    ): ResponseEntity<CommandResultResponse> {
        val correlationId = getOrGenerateCorrelationId(correlationIdHeader)
        val command = request.toCommand(userId)

        registerAddressCommandHandler.handle(command, correlationId)

        val response =
            CommandResultResponse(
                status = "SUCCESS",
                message = "주소가 등록되었습니다.",
                correlationId = correlationId,
            )
        response.add(linkTo(methodOn(UserQueryControllerV1::class.java).getUserProfile(userId, correlationId)).withRel("user-profile"))

        return ResponseEntity
            .created(
                linkTo(methodOn(UserQueryControllerV1::class.java).getUserProfile(userId, correlationId)).toUri(),
            ).body(response)
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "주소 수정", description = "등록된 배달 주소를 수정합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "주소 수정 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = CommandResultResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 데이터",
                content = [Content(mediaType = "application/problem+json")],
            ),
            ApiResponse(
                responseCode = "404",
                description = "사용자 또는 주소를 찾을 수 없음",
                content = [Content(mediaType = "application/problem+json")],
            ),
        ],
    )
    fun updateAddress(
        @Parameter(hidden = true) @RequestHeader("X-Correlation-Id", required = false) correlationIdHeader: String?,
        @Parameter(description = "사용자 ID", required = true) @PathVariable userId: String,
        @Parameter(description = "주소 ID", required = true) @PathVariable addressId: String,
        @Valid @RequestBody request: UpdateAddressRequestV1,
    ): ResponseEntity<CommandResultResponse> {
        val correlationId = getOrGenerateCorrelationId(correlationIdHeader)
        val command = request.toCommand(userId, addressId)

        updateAddressCommandHandler.handle(command, correlationId)

        val response =
            CommandResultResponse(
                status = "SUCCESS",
                message = "주소가 수정되었습니다.",
                correlationId = correlationId,
            )
        response.add(linkTo(methodOn(UserQueryControllerV1::class.java).getUserProfile(userId, correlationId)).withRel("user-profile"))

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "주소 삭제", description = "등록된 배달 주소를 삭제합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "주소 삭제 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = CommandResultResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "사용자 또는 주소를 찾을 수 없음",
                content = [Content(mediaType = "application/problem+json")],
            ),
        ],
    )
    fun deleteAddress(
        @Parameter(hidden = true) @RequestHeader("X-Correlation-Id", required = false) correlationIdHeader: String?,
        @Parameter(description = "사용자 ID", required = true) @PathVariable userId: String,
        @Parameter(description = "주소 ID", required = true) @PathVariable addressId: String,
    ): ResponseEntity<CommandResultResponse> {
        val correlationId = getOrGenerateCorrelationId(correlationIdHeader)
        val command = DeleteAddressCommand(userId, addressId)

        deleteAddressCommandHandler.handle(command, correlationId)

        val response =
            CommandResultResponse(
                status = "SUCCESS",
                message = "주소가 삭제되었습니다.",
                correlationId = correlationId,
            )
        response.add(linkTo(methodOn(UserQueryControllerV1::class.java).getUserProfile(userId, correlationId)).withRel("user-profile"))

        return ResponseEntity.ok(response)
    }
}
