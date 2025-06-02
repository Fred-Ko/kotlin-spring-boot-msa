package com.restaurant.user.presentation.v1.query.controller

import com.restaurant.user.application.query.GetAddressByIdQueryHandler
import com.restaurant.user.application.query.GetUserAddressesQueryHandler
import com.restaurant.user.application.query.dto.GetAddressByIdQuery
import com.restaurant.user.application.query.dto.GetUserAddressesQuery
import com.restaurant.user.domain.vo.AddressId
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.presentation.v1.query.dto.response.AddressDetailResponseV1
import com.restaurant.user.presentation.v1.query.dto.response.AddressResponseV1
import com.restaurant.user.presentation.v1.query.extensions.dto.response.toDetailResponseV1
import com.restaurant.user.presentation.v1.query.extensions.dto.response.toResponseV1
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/users/{userId}/addresses")
@Tag(name = "User Address Queries", description = "사용자 주소 조회 API")
class UserAddressQueryController(
    private val getUserAddressesQueryHandler: GetUserAddressesQueryHandler,
    private val getAddressByIdQueryHandler: GetAddressByIdQueryHandler,
) {
    @GetMapping
    @Operation(
        summary = "사용자 주소 목록 조회",
        description = "특정 사용자의 모든 주소를 조회합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "주소 목록 조회 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        array = ArraySchema(schema = Schema(implementation = AddressResponseV1::class)),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 형식 (Bad Request)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증되지 않은 사용자 (Unauthorized)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))],
            ),
            ApiResponse(
                responseCode = "403",
                description = "접근 권한 없음 (Forbidden)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음 (Not Found)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))],
            ),
        ],
    )
    fun getUserAddresses(
        @Parameter(description = "주소를 조회할 사용자의 ID", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        @PathVariable userId: UUID,
    ): ResponseEntity<List<AddressResponseV1>> {
        log.info { "Received request to get addresses for user ID: $userId" }
        val query = GetUserAddressesQuery(userId = UserId.of(userId).value.toString())
        val addresses = getUserAddressesQueryHandler.getUserAddresses(query)
        val responseList = addresses.map { it.toResponseV1() }

        log.info { "Returning ${responseList.size} addresses for user ID: $userId" }
        return ResponseEntity.ok(responseList)
    }

    @GetMapping("/{addressId}")
    @Operation(
        summary = "특정 주소 상세 조회",
        description = "사용자의 특정 주소 상세 정보를 조회합니다.",
        security = [SecurityRequirement(name = "bearerAuth")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "주소 상세 조회 성공",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = AddressDetailResponseV1::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 형식 (Bad Request)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증되지 않은 사용자 (Unauthorized)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))],
            ),
            ApiResponse(
                responseCode = "403",
                description = "접근 권한 없음 (Forbidden)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "사용자 또는 주소를 찾을 수 없음 (Not Found)",
                content = [Content(mediaType = "application/problem+json", schema = Schema(implementation = ProblemDetail::class))],
            ),
        ],
    )
    fun getAddressById(
        @Parameter(description = "주소를 조회할 사용자의 ID", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        @PathVariable userId: UUID,
        @Parameter(description = "조회할 주소의 ID", required = true, example = "b2c3d4e5-f6a7-8901-2345-67890abcdef1")
        @PathVariable addressId: UUID,
    ): ResponseEntity<AddressDetailResponseV1> {
        log.info { "Received request to get address ID: $addressId for user ID: $userId" }
        val query =
            GetAddressByIdQuery(
                userId = UserId.of(userId).value.toString(),
                addressId = AddressId.of(addressId).value.toString(),
            )
        val address = getAddressByIdQueryHandler.getAddressById(query)
        val response = address.toDetailResponseV1()

        log.info { "Returning address details for address ID: $addressId" }
        return ResponseEntity.ok(response)
    }
}
