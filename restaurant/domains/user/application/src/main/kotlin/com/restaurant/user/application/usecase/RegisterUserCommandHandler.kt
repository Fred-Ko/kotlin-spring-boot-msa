package com.restaurant.user.application.usecase

import com.restaurant.user.application.dto.command.RegisterUserCommand
import com.restaurant.user.application.port.RegisterUserUseCase
import com.restaurant.user.domain.aggregate.User
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.Email
import com.restaurant.user.domain.vo.Name
import com.restaurant.user.domain.vo.Password
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.domain.vo.Username
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterUserCommandHandler(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : RegisterUserUseCase {
    @Transactional
    override fun register(command: RegisterUserCommand): UserId {
        val username = Username.of(command.username)
        val email = Email.of(command.email)
        val name = Name.of(command.name)
        val password = Password.of(passwordEncoder.encode(command.password))
        val user =
            User.create(
                id = UserId.generate(),
                username = username,
                password = password,
                email = email,
                name = name,
                phoneNumber = null, // phoneNumber는 선택 사항이므로 null로 설정
            )
        userRepository.save(user)
        return user.id
    }
}
