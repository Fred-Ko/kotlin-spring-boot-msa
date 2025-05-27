package com.restaurant.user.presentation.v1.command.controller

import com.restaurant.common.presentation.dto.response.CommandResultResponse
import com.restaurant.user.application.command.usecase.RegisterAddressUseCase
import com.restaurant.user.application.command.usecase.UpdateAddressUseCase
import com.restaurant.user.application.command.usecase.DeleteAddressUseCase
import com.restaurant.user.application.command.dto.RegisterAddressCommand
import com.restaurant.user.application.command.dto.UpdateAddressCommand
import com.restaurant.user.application.command.dto.DeleteAddressCommand
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.presentation.v1.command.dto.request.RegisterAddressRequestV1
import com.restaurant.user.presentation.v1.command.dto.request.UpdateAddressRequestV1
import com.restaurant.user.presentation.v1.command.extensions.dto.request.toCommand
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.UUID

private val log = KotlinLogging.logger {}

@Tag(name = "User Address Commands", description = "사용자 주소 관리 API (생성/수정/삭제)")
@RestController
@RequestMapping("/api/v1/users/{userId}/addresses")
class UserAddressController(
    private val registerAddressUseCase: RegisterAddressUseCase,
    private val updateAddressUseCase: UpdateAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase
) {

    @PostMapping
    @Operation(
        summary = "사용자 주소 등록", 
        description = "특정 사용자의 새로운 주소를 등록합니다.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201", description = "주소 등록 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = CommandResultResponse::class))]
            ),
            ApiResponse(
                responseCode = "400", description = "잘못된 요청 형식 (Bad Request)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            ),
            ApiResponse(
                responseCode = "401", description = "인증되지 않은 사용자 (Unauthorized)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            ),
            ApiResponse(
                responseCode = "403", description = "접근 권한 없음 (Forbidden) - 다른 사용자의 주소 등록 시도 등",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            ),
            ApiResponse(
                responseCode = "404", description = "사용자를 찾을 수 없음 (Not Found)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            )
        ]
    )
    fun registerAddress(
        @Parameter(description = "주소를 등록할 사용자의 ID", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        @PathVariable userId: UUID,
        @Parameter(description = "주소 등록 요청 정보", required = true, schema = Schema(implementation = RegisterAddressRequestV1::class))
        @Valid @RequestBody request: RegisterAddressRequestV1
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
                message = "Address registered successfully. Address ID: ${addressId.value}"
            )
        )
    }

    @PutMapping("/{addressId}")
    @Operation(
        summary = "사용자 주소 수정", 
        description = "특정 사용자의 기존 주소 정보를 수정합니다.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "주소 수정 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = CommandResultResponse::class))]
            ),
            ApiResponse(
                responseCode = "400", description = "잘못된 요청 형식 (Bad Request)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            ),
            ApiResponse(
                responseCode = "401", description = "인증되지 않은 사용자 (Unauthorized)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            ),
            ApiResponse(
                responseCode = "403", description = "접근 권한 없음 (Forbidden) - 다른 사용자의 주소 수정 시도 등",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            ),
            ApiResponse(
                responseCode = "404", description = "사용자 또는 주소를 찾을 수 없음 (Not Found)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            )
        ]
    )
    fun updateAddress(
        @Parameter(description = "주소를 수정할 사용자의 ID", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        @PathVariable userId: UUID,
        @Parameter(description = "수정할 주소의 ID", required = true, example = "b2c3d4e5-f6a7-8901-2345-67890abcdef1")
        @PathVariable addressId: UUID,
        @Parameter(description = "주소 수정 요청 정보", required = true, schema = Schema(implementation = UpdateAddressRequestV1::class))
        @Valid @RequestBody request: UpdateAddressRequestV1
    ): ResponseEntity<CommandResultResponse> {
        log.info { "Received request to update address ID: $addressId for user ID: $userId" }
        val command = request.toCommand(UserId.of(userId), AddressId.of(addressId))
        updateAddressUseCase.updateAddress(command)
        return ResponseEntity.ok(
            CommandResultResponse(
                status = "SUCCESS",
                message = "Address updated successfully."
            )
        )
    }

    @DeleteMapping("/{addressId}")
    @Operation(
        summary = "사용자 주소 삭제", 
        description = "특정 사용자의 주소를 삭제합니다.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "주소 삭제 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = CommandResultResponse::class))]
            ),
            ApiResponse(
                responseCode = "401", description = "인증되지 않은 사용자 (Unauthorized)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            ),
            ApiResponse(
                responseCode = "403", description = "접근 권한 없음 (Forbidden) - 다른 사용자의 주소 삭제 시도 등",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            ),
            ApiResponse(
                responseCode = "404", description = "사용자 또는 주소를 찾을 수 없음 (Not Found)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))]
            )
        ]
    )
    fun deleteAddress(
        @Parameter(description = "주소를 삭제할 사용자의 ID", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        @PathVariable userId: UUID,
        @Parameter(description = "삭제할 주소의 ID", required = true, example = "b2c3d4e5-f6a7-8901-2345-67890abcdef1")
        @PathVariable addressId: UUID
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
                message = "Address deleted successfully."
            )
        )
    }
}
