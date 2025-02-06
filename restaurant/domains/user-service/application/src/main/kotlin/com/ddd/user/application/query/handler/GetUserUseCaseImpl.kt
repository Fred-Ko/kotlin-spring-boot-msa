package com.ddd.user.application.query.handler

import com.ddd.user.application.exception.UserApplicationException
import com.ddd.user.application.query.dto.UserDto
import com.ddd.user.application.query.dto.query.GetUserQuery
import com.ddd.user.application.query.usecase.GetUserUseCase
import com.ddd.user.domain.port.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetUserUseCaseImpl(private val userRepository: UserRepository) : GetUserUseCase {

    @Transactional(readOnly = true)
    override fun execute(query: GetUserQuery): UserDto {
        return try {
            val user =
                    userRepository.findById(query.id)
                            ?: throw UserApplicationException.UserNotFound(
                                    id = query.id.toString()
                            )

            return UserDto.from(user)
        } catch (e: Exception) {
            throw UserApplicationException.GetUserFailed(query.id.toString(), e)
        }
    }
}
