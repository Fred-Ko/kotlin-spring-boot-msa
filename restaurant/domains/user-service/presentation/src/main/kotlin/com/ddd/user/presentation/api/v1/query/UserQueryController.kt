package com.ddd.user.presentation.api.v1.query

import com.ddd.user.application.query.GetUserQuery
import com.ddd.user.application.query.ListUsersQuery
import com.ddd.user.presentation.api.v1.query.dto.response.GetUserResponse
import com.ddd.user.presentation.api.v1.query.dto.response.ListUsersResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "User Query API", description = "User Query API 입니다.")
@Validated
@RestController
@RequestMapping("/api/v1/users")
class UserQueryController(
        private val getUserQueryUseCase: GetUserQuery,
        private val listUsersQueryUseCase: ListUsersQuery,
) {
    @Operation(summary = "User 조회", description = "ID 로 User 를 조회합니다.")
    @GetMapping("/{id}")
    fun getUser(@PathVariable id: String): ResponseEntity<GetUserResponse> {
        val queryResult = getUserQueryUseCase.getUser(id)
        val response = GetUserResponse.fromQueryResult(queryResult)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "User 목록 조회", description = "User 목록을 페이지네이션으로 조회합니다.")
    @GetMapping
    fun listUsers(
            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(defaultValue = "0")
            @Min(0)
            page: Int,
            @Parameter(description = "페이지 크기 (최대 100)")
            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(100)
            size: Int,
    ): ResponseEntity<ListUsersResponse> {
        val queryResult = listUsersQueryUseCase.listUsers(page, size)
        val response = ListUsersResponse.fromQueryResult(queryResult)
        return ResponseEntity.ok(response)
    }
}
