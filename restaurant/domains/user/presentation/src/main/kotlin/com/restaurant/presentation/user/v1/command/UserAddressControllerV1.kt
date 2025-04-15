package com.restaurant.presentation.user.v1.command

import com.restaurant.application.user.command.handler.DeleteAddressCommandHandler
import com.restaurant.application.user.command.handler.RegisterAddressCommandHandler
import com.restaurant.application.user.command.handler.UpdateAddressCommandHandler
import com.restaurant.presentation.user.v1.dto.request.DeleteAddressRequestV1
import com.restaurant.presentation.user.v1.dto.request.RegisterAddressRequestV1
import com.restaurant.presentation.user.v1.dto.request.UpdateAddressRequestV1
import com.restaurant.presentation.user.v1.extensions.request.toCommand
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
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

    @PostMapping
    @Operation(summary = "주소 등록", description = "사용자의 새로운 배달 주소를 등록합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "주소 등록 성공",
                content = [Content(mediaType = "application/json")],
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
        @Parameter(description = "사용자 ID", required = true)
        @PathVariable userId: Long,
        @Valid @RequestBody request: RegisterAddressRequestV1,
    ): ResponseEntity<Any> {
        val correlationId = UUID.randomUUID().toString()
        val command = request.toCommand(userId)

        // Command 실행 - 실패 시 예외 발생하여 UserExceptionHandler에서 처리
        registerAddressCommandHandler.handle(command, correlationId)

        // 성공 시 응답
        val addressUri = "/api/v1/users/$userId/addresses"
        return ResponseEntity
            .created(URI.create(addressUri))
            .body(
                mapOf(
                    "status" to "SUCCESS",
                    "message" to "주소가 등록되었습니다.",
                    "correlationId" to correlationId,
                ),
            )
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "주소 수정", description = "등록된 배달 주소를 수정합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "주소 수정 성공",
                content = [Content(mediaType = "application/json")],
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
        @Parameter(description = "사용자 ID", required = true)
        @PathVariable userId: Long,
        @Parameter(description = "주소 ID", required = true)
        @PathVariable addressId: Long,
        @Valid @RequestBody request: UpdateAddressRequestV1,
    ): ResponseEntity<Any> {
        val correlationId = UUID.randomUUID().toString()
        val command = request.toCommand(userId, addressId)

        // Command 실행 - 실패 시 예외 발생하여 UserExceptionHandler에서 처리
        updateAddressCommandHandler.handle(command, correlationId)

        // 성공 시 응답
        return ResponseEntity
            .ok()
            .body(
                mapOf(
                    "status" to "SUCCESS",
                    "message" to "주소가 수정되었습니다.",
                    "correlationId" to correlationId,
                ),
            )
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "주소 삭제", description = "등록된 배달 주소를 삭제합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "주소 삭제 성공",
                content = [Content(mediaType = "application/json")],
            ),
            ApiResponse(
                responseCode = "404",
                description = "사용자 또는 주소를 찾을 수 없음",
                content = [Content(mediaType = "application/problem+json")],
            ),
        ],
    )
    fun deleteAddress(
        @Parameter(description = "사용자 ID", required = true)
        @PathVariable userId: Long,
        @Parameter(description = "주소 ID", required = true)
        @PathVariable addressId: Long,
    ): ResponseEntity<Any> {
        val correlationId = UUID.randomUUID().toString()
        val request = DeleteAddressRequestV1(addressId)
        val command = request.toCommand(userId)

        // Command 실행 - 실패 시 예외 발생하여 UserExceptionHandler에서 처리
        deleteAddressCommandHandler.handle(command, correlationId)

        // 성공 시 응답
        return ResponseEntity
            .ok()
            .body(
                mapOf(
                    "status" to "SUCCESS",
                    "message" to "주소가 삭제되었습니다.",
                    "correlationId" to correlationId,
                ),
            )
    }
}
