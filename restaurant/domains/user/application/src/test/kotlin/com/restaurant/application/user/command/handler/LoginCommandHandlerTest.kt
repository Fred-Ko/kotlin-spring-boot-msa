package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.LoginCommand
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
import java.time.LocalDateTime

class LoginCommandHandlerTest :
  BehaviorSpec({

    val userRepository = mockk<UserRepository>()
    val handler = LoginCommandHandler(userRepository)

    Given("등록된 사용자 정보로 로그인 요청을 보낼 때") {
      val email = "user@example.com"
      val password = "password123"
      val command = LoginCommand(email = email, password = password)

      val user =
        User.reconstitute(
          id = UserId(1L),
          email = Email(email),
          password = Password.of(password),
          name = "테스트유저",
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now(),
        )

      every { userRepository.findByEmail(Email(email)) } returns user

      When("로그인 처리를 하면") {
        val result = handler.handle(command)

        Then("성공 결과가 반환되어야 한다") {
          result.success shouldBe true
          result.correlationId.shouldNotBeEmpty()
        }
      }
    }

    Given("등록되지 않은 이메일로 로그인 요청을 보낼 때") {
      val command = LoginCommand(email = "unknown@example.com", password = "password123")

      every { userRepository.findByEmail(Email(command.email)) } returns null

      When("로그인 처리를 하면") {
        val result = handler.handle(command)

        Then("인증 실패 에러가 반환되어야 한다") {
          result.success shouldBe false
          result.errorCode shouldBe UserErrorCode.INVALID_CREDENTIALS.code
        }
      }
    }

    Given("등록된 이메일이지만 비밀번호가 틀린 로그인 요청을 보낼 때") {
      val email = "user@example.com"
      val password = "correctPassword123"
      val command = LoginCommand(email = email, password = "wrongPassword123")

      val user =
        User.reconstitute(
          id = UserId(1L),
          email = Email(email),
          password = Password.of(password), // 실제 비밀번호는 다름
          name = "테스트유저",
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now(),
        )

      every { userRepository.findByEmail(Email(email)) } returns user

      When("로그인 처리를 하면") {
        val result = handler.handle(command)

        Then("인증 실패 에러가 반환되어야 한다") {
          result.success shouldBe false
          result.errorCode shouldBe UserErrorCode.INVALID_CREDENTIALS.code
        }
      }
    }

    Given("유효하지 않은 이메일 형식으로 로그인 요청을 보낼 때") {
      val command = LoginCommand(email = "invalid-email", password = "password123")

      every { userRepository.findByEmail(any()) } throws IllegalArgumentException("유효한 이메일 형식이 아닙니다.")

      When("로그인 처리를 하면") {
        val result = handler.handle(command)

        Then("입력 유효성 에러가 반환되어야 한다") {
          result.success shouldBe false
          result.errorCode shouldBe UserErrorCode.INVALID_INPUT.code
        }
      }
    }
  })
