package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.RegisterUserCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify

class RegisterUserCommandHandlerTest :
  BehaviorSpec({

    val userRepository = mockk<UserRepository>()
    val handler = RegisterUserCommandHandler(userRepository)

    Given("유효한 사용자 정보로 회원가입 요청을 보낼 때") {
      val command =
        RegisterUserCommand(
          email = "test@example.com",
          password = "password123",
          name = "테스트유저",
        )

      val userSlot = slot<User>()
      every { userRepository.existsByEmail(any()) } returns false
      every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }

      When("회원가입 처리를 하면") {
        val result = handler.handle(command)

        Then("성공 결과가 반환되어야 한다") {
          result.success shouldBe true
          result.correlationId.shouldNotBeEmpty()
        }

        Then("사용자가 저장되어야 한다") {
          verify(exactly = 1) { userRepository.save(any()) }
          userSlot.captured.email.value shouldBe command.email
          userSlot.captured.name shouldBe command.name
        }
      }
    }

    Given("이미 가입된 이메일로 회원가입 요청을 보낼 때") {
      val command =
        RegisterUserCommand(
          email = "existing@example.com",
          password = "password123",
          name = "테스트유저",
        )

      every { userRepository.existsByEmail(Email(command.email)) } returns true

      When("회원가입 처리를 하면") {
        val result = handler.handle(command)

        Then("이메일 중복 에러가 반환되어야 한다") {
          result.success shouldBe false
          result.errorCode shouldBe UserErrorCode.DUPLICATE_EMAIL.code
        }

        Then("사용자가 저장되지 않아야 한다") {
          verify(exactly = 0) { userRepository.save(any()) }
        }
      }
    }

    Given("유효하지 않은 이메일 형식으로 회원가입 요청을 보낼 때") {
      val command =
        RegisterUserCommand(
          email = "invalid-email",
          password = "password123",
          name = "테스트유저",
        )

      every { userRepository.existsByEmail(any()) } throws IllegalArgumentException("유효한 이메일 형식이 아닙니다.")

      When("회원가입 처리를 하면") {
        val result = handler.handle(command)

        Then("입력 유효성 에러가 반환되어야 한다") {
          result.success shouldBe false
          result.errorCode shouldBe UserErrorCode.INVALID_INPUT.code
        }
      }
    }
  })
