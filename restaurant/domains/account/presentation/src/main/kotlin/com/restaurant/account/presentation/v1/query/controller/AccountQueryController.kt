package com.restaurant.account.presentation.v1.query.controller

import com.restaurant.account.application.query.dto.GetAccountByUserIdQuery
import com.restaurant.account.application.query.usecase.GetAccountByUserIdUseCase
import com.restaurant.account.domain.vo.UserId
import com.restaurant.account.presentation.v1.query.dto.response.AccountResponseV1
import com.restaurant.account.presentation.v1.query.extensions.dto.response.toResponseV1
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Account Query")
@RestController
@RequestMapping("/api/v1/accounts")
class AccountQueryController(
    private val getAccountByUserIdUseCase: GetAccountByUserIdUseCase,
) {
    @Operation(summary = "Get account by user ID")
    @GetMapping("/by-user/{userId}")
    fun getAccountByUserId(
        @PathVariable userId: String,
    ): ResponseEntity<AccountResponseV1> {
        val query = GetAccountByUserIdQuery(UserId.ofString(userId))
        val accountDto = getAccountByUserIdUseCase.getAccountByUserId(query)
        return accountDto?.let {
            ResponseEntity.ok(it.toResponseV1())
        } ?: ResponseEntity.notFound().build()
    }
}
