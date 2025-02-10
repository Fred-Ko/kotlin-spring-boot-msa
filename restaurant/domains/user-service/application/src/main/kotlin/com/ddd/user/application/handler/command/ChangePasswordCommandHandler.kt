package com.ddd.user.application.handler.command

import com.ddd.user.application.command.ChangePasswordCommand
import com.ddd.user.application.dto.command.ChangePasswordCommandDto
import com.ddd.user.application.dto.result.ChangePasswordResult
import com.ddd.user.application.exception.UserApplicationException
import com.ddd.user.domain.model.vo.Password
import com.ddd.user.domain.repository.UserRepository
import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChangePasswordCommandHandler(private val userRepository: UserRepository) :
        ChangePasswordCommand {
    @Transactional
    override fun changePassword(command: ChangePasswordCommandDto): ChangePasswordResult {
        val user =
                userRepository.findById(command.id)
                        ?: throw UserApplicationException.UserNotFound(command.id)

        val newPassword = Password(command.newPassword)
        val changedUser = user.changePassword(newPassword)
        userRepository.save(changedUser)

        return ChangePasswordResult(id = command.id)
    }
}
