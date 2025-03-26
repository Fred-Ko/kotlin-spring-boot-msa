package com.restaurant.domain.user.aggregate

import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.Password
import com.restaurant.domain.user.vo.UserId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class UserTest :
  FunSpec({

    test("유효한 정보로 사용자 생성 성공") {
      // given
      val email = Email("test@example.com")
      val password = Password.of("password123")
      val name = "테스트유저"

      // when
      val user = User.create(email, password, name)

      // then
      user.id.shouldBeNull()
      user.email shouldBe email
      user.password shouldBe password
      user.name shouldBe name
      user.createdAt.shouldNotBeNull()
      user.updatedAt.shouldNotBeNull()
    }

    test("기존 정보로 사용자 복원 성공") {
      // given
      val id = UserId(1L)
      val email = Email("test@example.com")
      val password = Password.of("password123")
      val name = "테스트유저"
      val createdAt = LocalDateTime.now().minusDays(1)
      val updatedAt = LocalDateTime.now()

      // when
      val user = User.reconstitute(id, email, password, name, createdAt, updatedAt)

      // then
      user.id shouldBe id
      user.email shouldBe email
      user.password shouldBe password
      user.name shouldBe name
      user.createdAt shouldBe createdAt
      user.updatedAt shouldBe updatedAt
    }

    test("사용자 프로필 업데이트 성공") {
      // given
      val user =
        User.create(
          Email("test@example.com"),
          Password.of("password123"),
          "원래이름",
        )
      val newName = "변경된이름"

      // when
      val updatedUser = user.updateProfile(newName)

      // then
      updatedUser.name shouldBe newName
      updatedUser.email shouldBe user.email
      updatedUser.password shouldBe user.password
      updatedUser.createdAt shouldBe user.createdAt
      // updatedAt은 최소한 원래 시간보다 크거나 같아야 함
      updatedUser.updatedAt shouldBeGreaterThanOrEqualTo user.updatedAt
    }

    test("사용자 비밀번호 변경 성공") {
      // given
      val originalPassword = "password123"
      val user =
        User.create(
          Email("test@example.com"),
          Password.of(originalPassword),
          "테스트유저",
        )
      val newPassword = "newpassword456"

      // when
      val updatedUser = user.changePassword(newPassword)

      // then
      updatedUser.checkPassword(originalPassword) shouldBe false
      updatedUser.checkPassword(newPassword) shouldBe true
      updatedUser.email shouldBe user.email
      updatedUser.name shouldBe user.name
      updatedUser.createdAt shouldBe user.createdAt
      updatedUser.updatedAt shouldBeGreaterThanOrEqualTo user.updatedAt
    }

    test("올바른 비밀번호 확인 성공") {
      // given
      val rawPassword = "password1234"
      val user =
        User.create(
          Email("test@example.com"),
          Password.of(rawPassword),
          "테스트유저",
        )

      // when & then
      user.checkPassword(rawPassword) shouldBe true
      user.checkPassword("wrongpassword") shouldBe false
    }
  })
