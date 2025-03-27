package com.restaurant.application.user.command.handler

import com.restaurant.application.user.TestConfig
import com.restaurant.application.user.command.DeleteUserCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.Name
import com.restaurant.domain.user.vo.Password
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [TestConfig::class])
@Transactional
@DirtiesContext
class DeleteUserCommandHandlerTest(
  @Autowired private val userRepository: UserRepository,
  @Autowired private val handler: DeleteUserCommandHandler,
) : BehaviorSpec({

    extension(SpringExtension)

    Given("올바른 비밀번호로 계정 삭제 요청을 보낼 때") {
      val password = "password123"

      // 테스트 사용자 생성
      val savedUser =
        userRepository.save(
          User.create(
            email = Email("delete-test-user@example.com"),
            password = Password.of(password),
            name = Name.of("테스트유저"),
          ),
        )

      val command =
        DeleteUserCommand(
          userId = savedUser.id!!.value,
          password = password,
        )

      When("계정 삭제 처리를 하면") {
        val result = handler.handle(command)

        Then("성공 결과가 반환되어야 한다") {
          result.success shouldBe true
          result.correlationId.shouldNotBeEmpty()
        }

        Then("사용자 계정이 삭제되어야 한다") {
          userRepository.findById(savedUser.id!!) shouldBe null
        }
      }
    }

    Given("존재하지 않는 사용자 ID로 계정 삭제 요청을 보낼 때") {
      val nonExistentUserId = 999L
      val command =
        DeleteUserCommand(
          userId = nonExistentUserId,
          password = "anypassword",
        )

      When("계정 삭제 처리를 하면") {
        val result = handler.handle(command)

        Then("사용자를 찾을 수 없음 오류가 반환되어야 한다") {
          result.success shouldBe false
          result.errorCode shouldBe UserErrorCode.NOT_FOUND.code
        }
      }
    }

    Given("잘못된 비밀번호로 계정 삭제 요청을 보낼 때") {
      val actualPassword = "actualPassword123"
      val wrongPassword = "wrongPassword123"

      // 테스트 사용자 생성
      val savedUser =
        userRepository.save(
          User.create(
            email = Email("delete-wrong-password-test-user@example.com"),
            password = Password.of(actualPassword),
            name = Name.of("테스트유저2"),
          ),
        )

      val command =
        DeleteUserCommand(
          userId = savedUser.id!!.value,
          password = wrongPassword,
        )

      When("계정 삭제 처리를 하면") {
        val result = handler.handle(command)

        Then("비밀번호 불일치 오류가 반환되어야 한다") {
          result.success shouldBe false
          result.errorCode shouldBe UserErrorCode.INVALID_CREDENTIALS.code
        }
      }
    }
  })
