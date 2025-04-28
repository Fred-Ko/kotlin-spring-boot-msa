package com.restaurant.application.user.handler

import com.restaurant.user.application.TestConfig
import com.restaurant.user.application.common.UserErrorCode
import com.restaurant.user.application.dto.command.ChangePasswordCommand
import com.restaurant.user.domain.aggregate.User
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.Email
import com.restaurant.user.domain.vo.Name
import com.restaurant.user.domain.vo.Password
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [TestConfig::class])
@Transactional
@DirtiesContext
class ChangePasswordCommandHandlerTest(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val handler: ChangePasswordCommandHandler,
) : StringSpec({

        "올바른 현재 비밀번호로 비밀번호 변경 요청을 보낼 때 비밀번호가 업데이트되어야 한다" {
            val currentPassword = "currentPassword123"
            val newPassword = "newPassword456"

            val savedUser =
                userRepository.save(
                    User.create(
                        email = Email.of("test1@example.com"),
                        password = Password.of(currentPassword),
                        name = Name.of("테스트유저"),
                    ),
                )

            val command =
                ChangePasswordCommand(
                    userId = savedUser.id!!.value,
                    currentPassword = currentPassword,
                    newPassword = newPassword,
                )

            // 비밀번호 변경 실행
            handler.handle(command)

            // 업데이트된 사용자 확인
            val updatedUser = userRepository.findById(savedUser.id!!)!!
            updatedUser.checkPassword(newPassword) shouldBe true
            updatedUser.checkPassword(currentPassword) shouldBe false
        }

        "존재하지 않는 사용자 ID로 비밀번호 변경 요청을 보낼 때 예외가 발생해야 한다" {
            val nonExistentUserId = 999L
            val command =
                ChangePasswordCommand(
                    userId = nonExistentUserId,
                    currentPassword = "anypassword",
                    newPassword = "newpassword",
                )

            // 예외 발생 확인
            val exception =
                shouldThrow<UserApplicationException.Query.NotFound> {
                    handler.handle(command)
                }

            exception.errorCode shouldBe UserErrorCode.NOT_FOUND
        }

        "잘못된 현재 비밀번호로 비밀번호 변경 요청을 보낼 때 예외가 발생해야 한다" {
            val actualPassword = "actualPassword123"
            val wrongPassword = "wrongPassword123"
            val newPassword = "newPassword456"

            val savedUser =
                userRepository.save(
                    User.create(
                        email = Email.of("test2@example.com"),
                        password = Password.of(actualPassword),
                        name = Name.of("테스트유저2"),
                    ),
                )

            val command =
                ChangePasswordCommand(
                    userId = savedUser.id!!.value,
                    currentPassword = wrongPassword,
                    newPassword = newPassword,
                )

            // 예외 발생 확인
            val exception =
                shouldThrow<UserApplicationException.Password.CurrentPasswordMismatch> {
                    handler.handle(command)
                }

            exception.errorCode shouldBe UserErrorCode.CURRENT_PASSWORD_MISMATCH
        }
    })
