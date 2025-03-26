package com.restaurant.application.user.command.handler

import com.restaurant.application.user.command.UpdateProfileCommand
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

class UpdateProfileCommandHandlerTest :
  BehaviorSpec({

    val userRepository = mockk<UserRepository>()
    val handler = UpdateProfileCommandHandler(userRepository)

    Given("존재하는 사용자 ID로 프로필 업데이트 요청을 보낼 때") {
      val userId = UserId(1L)
      val oldName = "원래이름"
      val newName = "새이름"
      val command =
        UpdateProfileCommand(
          userId = userId.value,
          name = newName,
        )

      val user =
        User.reconstitute(
          id = userId,
          email = Email("user@example.com"),
          password = Password.of("password123"),
          name = oldName,
          createdAt = LocalDateTime.now(),
          updatedAt = LocalDateTime.now(),
        )

      val userSlot = slot<User>()

      every { userRepository.findById(userId) } returns user
      every { userRepository.save(capture(userSlot)) } answers { userSlot.captured }

      When("프로필 업데이트 처리를 하면") {
        val result = handler.handle(command)

        Then("성공 결과가 반환되어야 한다") {
          result.success shouldBe true
          result.correlationId.shouldNotBeEmpty()
        }

        Then("이름이 변경된 사용자가 저장되어야 한다") {
          verify(exactly = 1) { userRepository.save(any()) }
          userSlot.captured.name shouldBe newName
          userSlot.captured.id shouldBe userId
          userSlot.captured.email shouldBe user.email
        }
      }
    }

    Given("존재하지 않는 사용자 ID로 프로필 업데이트 요청을 보낼 때") {
      val userId = UserId(99L)
      val command =
        UpdateProfileCommand(
          userId = userId.value,
          name = "아무이름",
        )

      every { userRepository.findById(userId) } returns null

      When("프로필 업데이트 처리를 하면") {
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

    Given("시스템 에러가 발생할 때") {
      val userId = UserId(1L)
      val command =
        UpdateProfileCommand(
          userId = userId.value,
          name = "새이름",
        )

      every { userRepository.findById(userId) } throws RuntimeException("시스템 오류")

      When("프로필 업데이트 처리를 하면") {
        val result = handler.handle(command)

        Then("시스템 에러가 반환되어야 한다") {
          result.success shouldBe false
          result.errorCode shouldBe UserErrorCode.SYSTEM_ERROR.code
        }
      }
    }
  })
