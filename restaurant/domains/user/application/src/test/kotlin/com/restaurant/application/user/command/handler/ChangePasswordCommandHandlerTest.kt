package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.ChangePasswordCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.Password
import com.restaurant.domain.user.vo.UserId
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.time.LocalDateTime

class ChangePasswordCommandHandlerTest :
  BehaviorSpec({

    val userRepository = mockk<UserRepository>()
    val handler = ChangePasswordCommandHandler(userRepository)

    Given("올바른 현재 비밀번호로 비밀번호 변경 요청을 보낼 때") {
      val userId = UserId(1L)
      val currentPassword = "currentPassword123"
      val newPassword = "newPassword456"
      val command =
        ChangePasswordCommand(
          userId = userId.value,
          currentPassword = currentPassword,
          newPassword = newPassword,
        )

      val user =
        User.reconstitute(
          id = userId,
          email = Email("user@example.com"),
          password = Password.of(currentPassword),
          name = "테스트유저",
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now(),
        )

      val userSlot = slot<User>()

      every { userRepository.findById(userId) } returns user
      every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }

      When("비밀번호 변경 처리를 하면") {
        val result = handler.handle(command)

        Then("성공 결과가 반환되어야 한다") {
          result.success shouldBe true
          result.correlationId.shouldNotBeEmpty()
        }

        Then("변경된 비밀번호로 사용자가 저장되어야 한다") {
          verify(exactly = 1) { userRepository.save(any()) }
          userSlot.captured.checkPassword(newPassword) shouldBe true
          userSlot.captured.checkPassword(currentPassword) shouldBe false
        }
      }
    }

    Given("존재하지 않는 사용자 ID로 비밀번호 변경 요청을 보낼 때") {
      val userId = UserId(99L)
      val command =
        ChangePasswordCommand(
          userId = userId.value,
          currentPassword = "anypassword",
          newPassword = "newpassword",
        )

      every { userRepository.findById(userId) } returns null

      When("비밀번호 변경 처리를 하면") {
        val result = handler.handle(command)

        Then("사용자 없음 에러가 반환되어야 한다") {
          result.success shouldBe false
          result.errorCode shouldBe UserErrorCode.NOT_FOUND.code
        }

        Then("사용자가 저장되지 않아야 한다") {
          verify(exactly = 0) { userRepository.save(any()) }
        }
      }
    }

    Given("잘못된 현재 비밀번호로 비밀번호 변경 요청을 보낼 때") {
      val userId = UserId(1L)
      val actualPassword = "actualPassword123"
      val wrongPassword = "wrongPassword123"
      val newPassword = "newPassword456"
      val command =
        ChangePasswordCommand(
          userId = userId.value,
          currentPassword = wrongPassword,
          newPassword = newPassword,
        )

      val user =
        User.reconstitute(
          id = userId,
          email = Email("user@example.com"),
          password = Password.of(actualPassword), // 실제 비밀번호와 다름
          name = "테스트유저",
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now(),
        )

      every { userRepository.findById(userId) } returns user

      When("비밀번호 변경 처리를 하면") {
        val result = handler.handle(command)

        Then("잘못된 비밀번호 에러가 반환되어야 한다") {
          result.success shouldBe false
          result.errorCode shouldBe UserErrorCode.INVALID_PASSWORD.code
        }

        Then("사용자가 저장되지 않아야 한다") {
          verify(exactly = 0) { userRepository.save(any()) }
        }
      }
    }
  })
