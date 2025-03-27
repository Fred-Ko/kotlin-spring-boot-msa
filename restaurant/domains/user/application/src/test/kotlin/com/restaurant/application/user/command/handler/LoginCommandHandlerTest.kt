package com.restaurant.application.user.command.handler

import com.restaurant.application.user.TestConfig
import com.restaurant.application.user.command.LoginCommand
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
class LoginCommandHandlerTest(
  @Autowired private val userRepository: UserRepository,
  @Autowired private val handler: LoginCommandHandler,
) : BehaviorSpec({

    extension(SpringExtension)

    Given("올바른 이메일과 비밀번호로 로그인을 시도할 때") {
      val userEmail = "login-test-user@example.com"
      val userPassword = "password123"
      val command = LoginCommand(email = userEmail, password = userPassword)

      // 테스트 사용자 생성
      userRepository.save(
        User.create(
          email = Email(userEmail),
          password = Password.of(userPassword),
          name = Name.of("테스트유저"),
        ),
      )

      When("로그인 처리를 하면") {
        val result = handler.handle(command)

        Then("로그인 성공 응답이 반환되어야 한다") {
          result.success shouldBe true
          result.correlationId.shouldNotBeEmpty()
        }
      }
    }

    Given("가입되지 않은 이메일로 로그인을 시도할 때") {
      val nonExistentEmail = "nonexistent@example.com"
      val command = LoginCommand(email = nonExistentEmail, password = "anypassword")

      When("로그인 처리를 하면") {
        val result = handler.handle(command)

        Then("사용자 찾을 수 없음 에러가 반환되어야 한다") {
          result.success shouldBe false
          result.errorCode shouldBe UserErrorCode.INVALID_CREDENTIALS.code
        }
      }
    }

    Given("올바르지 않은 비밀번호로 로그인을 시도할 때") {
      val userEmail = "login-wrong-password-test-user@example.com"
      val actualPassword = "actualPassword"
      val wrongPassword = "wrongPassword"
      val command = LoginCommand(email = userEmail, password = wrongPassword)

      // 테스트 사용자 생성
      userRepository.save(
        User.create(
          email = Email(userEmail),
          password = Password.of(actualPassword),
          name = Name.of("테스트유저2"),
        ),
      )

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

      When("로그인 처리를 하면") {
        val result = handler.handle(command)

        Then("입력 유효성 에러가 반환되어야 한다") {
          result.success shouldBe false
          result.errorCode shouldBe UserErrorCode.INVALID_INPUT.code
        }
      }
    }
  })
