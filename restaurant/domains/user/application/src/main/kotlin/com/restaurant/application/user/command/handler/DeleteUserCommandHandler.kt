package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.DeleteUserCommand
import com.restaurant.application.user.exception.UserDeletionException
import com.restaurant.common.core.command.CommandResult

import com.restaurant.domain.user.exception.InvalidCredentialsException
import com.restaurant.domain.user.exception.UserNotFoundException
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.UserId
import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteUserCommandHandler(private val userRepository: UserRepository) {
    @Transactional
    fun handle(command: DeleteUserCommand): CommandResult {
        try {
            val userId = UserId(command.userId)
            val user =
                    userRepository.findById(userId)
                            ?: throw UserNotFoundException(userId.toString())

            if (!user.checkPassword(command.password)) {
                throw InvalidCredentialsException()
            }

            userRepository.delete(user)

            return CommandResult(true, UUID.randomUUID().toString())
        } catch (e: UserNotFoundException) {
            throw UserDeletionException("사용자를 찾을 수 없습니다: ${command.userId}")
        } catch (e: InvalidCredentialsException) {
            throw UserDeletionException("비밀번호가 올바르지 않습니다.")
        } catch (e: IllegalArgumentException) {
            throw UserDeletionException(e.message ?: "유효하지 않은 입력값입니다.")
        } catch (e: Exception) {
            throw UserDeletionException("회원 탈퇴 중 오류가 발생했습니다: ${e.message}")
        }
    }
}
