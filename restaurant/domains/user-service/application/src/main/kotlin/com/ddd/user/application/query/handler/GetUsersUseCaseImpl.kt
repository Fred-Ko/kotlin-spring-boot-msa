package com.ddd.user.application.query.handler

import com.ddd.user.application.query.dto.UserDto
import com.ddd.user.application.query.query.GetUsersQuery
import com.ddd.user.application.query.result.GetUsersResult
import com.ddd.user.application.query.usecase.GetUsersUseCase
import com.ddd.user.domain.model.aggregate.User
import com.ddd.user.domain.port.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetUsersUseCaseImpl(private val userRepository: UserRepository) : GetUsersUseCase {

    @Transactional(readOnly = true)
    override fun execute(query: GetUsersQuery): GetUsersResult {
        val pageRequest = PageRequest.of(query.page, query.size)
        val usersPage: Page<User> = userRepository.findAll(pageRequest)
        return try {
            GetUsersResult.Success(usersPage.map { UserDto.from(it) })
        } catch (e: Exception) {
            GetUsersResult.Failure.ValidationError(e.message ?: "Unknown error")
        }
    }
}
