package com.ddd.user.application.query.handler

import com.ddd.user.application.exception.UserApplicationException
import com.ddd.user.application.query.dto.UserDto
import com.ddd.user.application.query.dto.query.GetUsersQuery
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
    override fun execute(query: GetUsersQuery): Page<UserDto> {
        return try {
        val pageRequest = PageRequest.of(query.page, query.size)
        val usersPage: Page<User> = userRepository.findAll(pageRequest)

        return usersPage.map( UserDto::from )
        } catch (e: Exception) {
            throw UserApplicationException.GetUsersFailed(query.page,query.size, e)
        }
    }
}
