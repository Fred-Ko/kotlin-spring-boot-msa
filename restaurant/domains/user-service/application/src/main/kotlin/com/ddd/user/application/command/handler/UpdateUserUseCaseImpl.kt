package com.ddd.user.application.command.handler

import com.ddd.user.application.command.dto.command.UpdateUserCommand
import com.ddd.user.application.command.dto.result.UpdateUserResult
import com.ddd.user.application.command.usecase.UpdateUserUseCase
import com.ddd.user.application.exception.UserApplicationException
import com.ddd.user.domain.model.vo.*
import com.ddd.user.domain.port.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateUserUseCaseImpl(private val userRepository: UserRepository) : UpdateUserUseCase {

    @Transactional
    override fun execute(command: UpdateUserCommand): UpdateUserResult {
        return try {
            val user =
                    userRepository.findById(command.id)
                            ?: throw UserApplicationException.UserNotFound(command.id.toString())

            user.updateProfile(
                    name = command.name?.let { UserName.of(it) },
                    phoneNumber = command.phoneNumber?.let { PhoneNumber.of(it) },
                    address =
                            command.address?.let {
                                Address.of(
                                        street = it.street,
                                        city = it.city,
                                        state = it.state,
                                        zipCode = it.zipCode
                                )
                            }
            )

            val savedUser = userRepository.save(user)
            return UpdateUserResult.from(savedUser)
        } catch (e: Exception) {
            throw UserApplicationException.UpdateUserFailed(command.id.toString(), e)
        }
    }
}
