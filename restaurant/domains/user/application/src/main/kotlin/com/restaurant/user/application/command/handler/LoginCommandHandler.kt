package com.restaurant.user.application.command.handler

import com.restaurant.user.application.command.dto.LoginCommand
import com.restaurant.user.application.command.usecase.LoginUseCase
import com.restaurant.user.application.exception.UserApplicationException
import com.restaurant.user.application.query.dto.LoginResult
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.Email
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LoginCommandHandler(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : LoginUseCase {
    @Transactional(readOnly = true)
    override fun login(command: LoginCommand): LoginResult {
        try {
            val emailVo = Email.of(command.email)
            val user =
                userRepository.findByEmail(emailVo)
                    ?: throw UserDomainException.User.InvalidCredentials(command.email)

            if (!passwordEncoder.matches(command.password, user.password.value)) {
                throw UserDomainException.User.InvalidCredentials(command.email)
            }

            val accessToken = "dummy-access-token"
            val refreshToken = "dummy-refresh-token"

            return LoginResult(
                id = user.id.value.toString(),
                username = user.username.value,
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        } catch (de: UserDomainException.User.InvalidCredentials) {
            throw de
        } catch (iae: IllegalArgumentException) {
            throw UserApplicationException.BadRequest("Invalid email or password format.", iae)
        } catch (e: Exception) {
            throw UserApplicationException.UnexpectedError(message = "Login failed due to an unexpected error.", cause = e)
        }
    }
}
