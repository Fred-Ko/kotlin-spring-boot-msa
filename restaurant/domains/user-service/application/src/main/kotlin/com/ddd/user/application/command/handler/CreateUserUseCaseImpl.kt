package com.ddd.user.application.command.handler

import com.ddd.user.application.command.dto.command.CreateUserCommand
import com.ddd.user.application.command.dto.result.CreateUserResult
import com.ddd.user.application.command.usecase.CreateUserUseCase
import com.ddd.user.application.exception.UserApplicationException
import com.ddd.user.domain.model.aggregate.User
import com.ddd.user.domain.model.vo.*
import com.ddd.user.domain.port.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateUserUseCaseImpl(private val userRepository: UserRepository) : CreateUserUseCase {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun execute(command: CreateUserCommand): CreateUserResult {
        return try {
            Email.of(command.email).takeIf { userRepository.existsByEmail(it) }?.let {
                throw UserApplicationException.EmailAlreadyExists(
                        "User with email ${command.email} already exists"
                )
            }

            val user =
                    User.create(
                            name = UserName.of(command.name.name),
                            email = Email.of(command.email),
                            phoneNumber = PhoneNumber.of(command.phoneNumber.number),
                            address =
                                    Address.of(
                                            street = command.address.street,
                                            city = command.address.city,
                                            state = command.address.state,
                                            zipCode = command.address.zipCode
                                    )
                    )

            val savedUser = userRepository.save(user)
            return CreateUserResult.from(savedUser)
        } catch (e: Exception) {
            logger.error("User creation failed: ${e.message}", e)
            throw UserApplicationException.UserCreationFailed(
                    "Failed to create user: ${e.message}",
                    e
            )
        }
    }
}
