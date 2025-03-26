package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.DeleteUserCommand
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
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import java.time.LocalDateTime

class DeleteUserCommandHandlerTest :
  BehaviorSpec({

    val userRepository = mockk<UserRepository>()
    val handler = DeleteUserCommandHandler(userRepository)

    Given("올바른 비밀번호로 사용자 삭제 요청을 보낼 때") {
      val userId = UserId(1L)
      val password = "password123"
      val command =
        DeleteUserCommand(
          userId = userId.value,
          password = password,
        )

      val user =
        User.reconstitute(
          id = userId,
          email = Email("user@example.com"),
          password = Password.of(password),
          name = "테스트유저",
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now(),
        )

      every { userRepository.findById(userId) } returns user
      every { userRepository.delete(user) } just runs

      When("사용자 삭제 처리를 하면") {
        val result = handler.handle(command)

        Then("성공 결과가 반환되어야 한다") {
          result.success shouldBe true
          result.correlationId.shouldNotBeEmpty()
        }

        Then("사용자가 삭제되어야 한다") {
          verify(exactly = 1) { userRepository.delete(user) }
        }
      }
    }

    Given("존재하지 않는 사용자 ID로 삭제 요청을 보낼 때") {
      val userId = UserId(99L)
      val command =
        DeleteUserCommand(
          userId = userId.value,
          password = "anypassword",
        )

      every { userRepository.findById(userId) } returns null

      When("사용자 삭제 처리를 하면") {
        val result = handler.handle(command)

        Then("사용자 없음 에러가 반환되어야 한다") {
          result.success shouldBe false
          result.errorCode shouldBe UserErrorCode.NOT_FOUND.code
        }

        Then("삭제 작업이 수행되지 않아야 한다") {
          verify(exactly = 0) { userRepository.delete(any()) }
        }
      }
    }

    Given("잘못된 비밀번호로 사용자 삭제 요청을 보낼 때") {
      val userId = UserId(1L)
      val actualPassword = "actualPassword123"
      val wrongPassword = "wrongPassword123"
      val command =
        DeleteUserCommand(
          userId = userId.value,
          password = wrongPassword,
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

      When("사용자 삭제 처리를 하면") {
        val result = handler.handle(command)

        Then("잘못된 비밀번호 에러가 반환되어야 한다") {
          result.success shouldBe false
          result.errorCode shouldBe UserErrorCode.INVALID_PASSWORD.code
        }

        Then("삭제 작업이 수행되지 않아야 한다") {
          verify(exactly = 0) { userRepository.delete(any()) }
        }
      }
    }

    Given("삭제 중 예외가 발생할 때") {
      val userId = UserId(1L)
      val password = "password123"
      val command =
        DeleteUserCommand(
          userId = userId.value,
          password = password,
        )

      val user =
        User.reconstitute(
          id = userId,
          email = Email("user@example.com"),
          password = Password.of(password),
          name = "테스트유저",
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now(),
        )

      every { userRepository.findById(userId) } returns user
      every { userRepository.delete(user) } throws RuntimeException("삭제 중 오류 발생")

      When("사용자 삭제 처리를 하면") {
        val result = handler.handle(command)

        Then("삭제 실패 에러가 반환되어야 한다") {
          result.success shouldBe false
          result.errorCode shouldBe UserErrorCode.DELETION_FAILED.code
        }
      }
    }
  })
