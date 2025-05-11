package com.restaurant.user.application.usecase

import com.restaurant.user.application.dto.command.LoginCommand
import com.restaurant.user.application.dto.query.LoginResult
import com.restaurant.user.application.exception.UserApplicationException
import com.restaurant.user.application.port.input.LoginUseCase
import com.restaurant.user.domain.exception.UserDomainException
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.Email // Username 대신 Email import
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
            // username 대신 email 사용
            val emailVo = Email.of(command.email)
            // findByUsername 대신 findByEmail 사용 (UserRepository에 해당 메소드 필요)
            val user = userRepository.findByEmail(emailVo)
                ?: throw UserDomainException.User.InvalidCredentials(command.email)

            if (!passwordEncoder.matches(command.password, user.password.value)) {
                throw UserDomainException.User.InvalidCredentials(command.email)
            }

            // 실제 토큰 생성 로직은 여기에 구현되어야 합니다.
            // 임시로 빈 문자열을 사용합니다.
            val accessToken = "dummy-access-token"
            val refreshToken = "dummy-refresh-token"

            return LoginResult(
                userId = user.id.value.toString(),
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
