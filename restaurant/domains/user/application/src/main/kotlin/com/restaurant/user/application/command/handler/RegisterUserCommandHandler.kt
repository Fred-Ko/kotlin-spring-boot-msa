package com.restaurant.user.application.command.handler

import com.restaurant.user.application.command.IRegisterUserCommandHandler
import com.restaurant.user.application.command.dto.RegisterUserCommand
import com.restaurant.user.domain.aggregate.User
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.Email
import com.restaurant.user.domain.vo.Name
import com.restaurant.user.domain.vo.Password
import com.restaurant.user.domain.vo.PhoneNumber
import com.restaurant.user.domain.vo.UserId
import com.restaurant.user.domain.vo.UserType
import com.restaurant.user.domain.vo.Username
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterUserCommandHandler(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : IRegisterUserCommandHandler {
    @Transactional
    override fun register(command: RegisterUserCommand): UserId {
        val username = Username.of(command.username)
        val email = Email.of(command.email)
        val name = Name.of(command.name)
        val password = Password.of(passwordEncoder.encode(command.password))
        val phoneNumberVo = command.phoneNumber?.let { PhoneNumber.of(it) }
        val user =
            User.create(
                id = UserId.generate(),
                username = username,
                password = password,
                email = email,
                name = name,
                phoneNumber = phoneNumberVo,
                userType = UserType.CUSTOMER,
            )
        userRepository.save(user)
        return user.id
    }
}
