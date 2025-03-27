package com.restaurant.application.user.command.handler

import com.restaurant.application.user.TestConfig
import com.restaurant.application.user.command.UpdateProfileCommand
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
class UpdateProfileCommandHandlerTest(
  @Autowired private val userRepository: UserRepository,
  @Autowired private val handler: UpdateProfileCommandHandler,
) : StringSpec({

    "존재하는 사용자 ID로 프로필 업데이트 요청을 보낼 때 이름이 업데이트되어야 한다" {
      // 테스트 사용자 생성
      val oldName = "원래이름"
      val newName = "새이름"

      val savedUser =
        userRepository.save(
          User.create(
            email = Email("profile-update-test-user@example.com"),
            password = Password.of("password123"),
            name = Name.of(oldName),
          ),
        )

      val command =
        UpdateProfileCommand(
          userId = savedUser.id!!.value,
          name = newName,
        )

      // 프로필 업데이트 실행
      val result = handler.handle(command)

      // 검증
      result.success shouldBe true
      result.correlationId.shouldNotBeEmpty()

      // 업데이트된 사용자 확인
      val updatedUser = userRepository.findById(savedUser.id!!)!!
      updatedUser.name.value shouldBe newName
    }

    "존재하지 않는 사용자 ID로 프로필 업데이트 요청을 보낼 때 실패 결과가 반환되어야 한다" {
      val nonExistentUserId = 999L
      val command =
        UpdateProfileCommand(
          userId = nonExistentUserId,
          name = "아무이름",
        )

      // 결과 확인
      val result = handler.handle(command)

      result.success shouldBe false
      result.errorCode shouldBe UserErrorCode.NOT_FOUND.code
    }
  })
