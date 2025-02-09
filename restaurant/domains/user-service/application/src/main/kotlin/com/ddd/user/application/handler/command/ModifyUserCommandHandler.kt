package com.ddd.user.application.handler.command

import com.ddd.user.application.command.ModifyUserCommand
import com.ddd.user.application.command.dto.result.ModifyUserResult
import com.ddd.user.application.dto.command.ModifyUserCommandDto
import com.ddd.user.application.exception.UserApplicationException
import com.ddd.user.domain.model.vo.Address
import com.ddd.user.domain.model.vo.UserName
import com.ddd.user.domain.port.repository.UserRepository
import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ModifyUserCommandHandler(private val userRepository: UserRepository) : ModifyUserCommand {
    @Transactional
    override fun modifyUser(command: ModifyUserCommandDto): ModifyUserResult {
        val user =
                userRepository.findById(UUID.fromString(command.id))
                        ?: throw UserApplicationException.UserNotFound(command.id)

        val newName = UserName.of(command.name)
        val newAddress = Address.of(command.street, command.city, command.state, command.zipCode)

        val modifiedUser = user.modifyUser(newName, newAddress)
        userRepository.save(modifiedUser)

        return ModifyUserResult(id = command.id)
    }
}
