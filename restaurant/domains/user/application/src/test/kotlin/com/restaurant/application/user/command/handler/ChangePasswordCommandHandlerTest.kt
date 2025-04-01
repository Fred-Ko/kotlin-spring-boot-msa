package com.restaurant.application.user.command.handler

import com.restaurant.application.user.TestConfig
import com.restaurant.application.user.command.ChangePasswordCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.Name
import com.restaurant.domain.user.vo.Password
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
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
                        email = Email("test1@example.com"),
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
            val result = handler.handle(command)

            // 검증
            result.success shouldBe true
            result.correlationId.shouldNotBeEmpty()

            // 업데이트된 사용자 확인
            val updatedUser = userRepository.findById(savedUser.id!!)!!
            updatedUser.checkPassword(newPassword) shouldBe true
            updatedUser.checkPassword(currentPassword) shouldBe false
        }

        "존재하지 않는 사용자 ID로 비밀번호 변경 요청을 보낼 때 실패 결과가 반환되어야 한다" {
            val nonExistentUserId = 999L
            val command =
                ChangePasswordCommand(
                    userId = nonExistentUserId,
                    currentPassword = "anypassword",
                    newPassword = "newpassword",
                )

            // 결과 확인
            val result = handler.handle(command)

            result.success shouldBe false
            result.errorCode shouldBe UserErrorCode.NOT_FOUND.code
        }

        "잘못된 현재 비밀번호로 비밀번호 변경 요청을 보낼 때 실패 결과가 반환되어야 한다" {
            val actualPassword = "actualPassword123"
            val wrongPassword = "wrongPassword123"
            val newPassword = "newPassword456"

            val savedUser =
                userRepository.save(
                    User.create(
                        email = Email("test2@example.com"),
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

            // 결과 확인
            val result = handler.handle(command)

            result.success shouldBe false
            result.errorCode shouldBe UserErrorCode.INVALID_PASSWORD.code
        }
    })
