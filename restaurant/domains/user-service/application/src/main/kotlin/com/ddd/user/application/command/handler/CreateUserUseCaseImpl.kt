package com.ddd.user.application.command.handler

import com.ddd.user.application.command.command.CreateUserCommand
import com.ddd.user.application.command.result.CreateUserResult
import com.ddd.user.application.command.usecase.CreateUserUseCase
import com.ddd.user.domain.model.aggregate.User
import com.ddd.user.domain.model.vo.*
import com.ddd.user.domain.port.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateUserUseCaseImpl(private val userRepository: UserRepository) : CreateUserUseCase {

    @Transactional
    override fun execute(command: CreateUserCommand): CreateUserResult {
        return try {
            if (userRepository.existsByEmail(Email.of(command.email))) {
                return CreateUserResult.Failure.EmailAlreadyExists(command.email)
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
            CreateUserResult.Success(savedUser.id.toString())
        } catch (e: Exception) {
            CreateUserResult.Failure.ValidationError(e.message ?: "Unknown error")
        }
    }
}
