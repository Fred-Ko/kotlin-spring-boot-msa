package com.ddd.user.application.handler.command

import com.ddd.user.application.command.RegisterUserCommand
import com.ddd.user.application.command.dto.result.RegisterUserResult
import com.ddd.user.application.dto.command.RegisterUserCommandDto
import com.ddd.user.application.exception.UserApplicationException
import com.ddd.user.domain.model.aggregate.User
import com.ddd.user.domain.model.vo.*
import com.ddd.user.domain.port.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterUserCommandHandler(private val userRepository: UserRepository) : RegisterUserCommand {
    @Transactional
    override fun registerUser(command: RegisterUserCommandDto): RegisterUserResult {
        val email = Email.of(command.email)

        if (userRepository.existsByEmail(email)) {
            throw UserApplicationException.EmailAlreadyExists(command.email)
        }

        val user =
                User.create(
                        name = UserName.of(command.name),
                        email = email,
                        password = Password(command.password),
                        phoneNumber = PhoneNumber.of(command.phoneNumber),
                        address =
                                Address.of(
                                        command.street,
                                        command.city,
                                        command.state,
                                        command.zipCode
                                )
                )

        val savedUser = userRepository.save(user)
        return RegisterUserResult(id = savedUser.id.toString())
    }
}
