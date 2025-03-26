package com.restaurant.domain.user.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeEmpty

class PasswordTest :
  FunSpec({

    test("유효한 비밀번호로 객체 생성 성공") {
      // given
      val validPassword = "password1234"

      // when
      val password = Password.of(validPassword)

      // then
      password.encodedValue shouldNotBe validPassword // 암호화되었는지 확인
      password.encodedValue.shouldNotBeEmpty()
    }

    test("최소 길이보다 짧은 비밀번호로 객체 생성 시 예외 발생") {
      // given
      val shortPassword = "123"

      // then
      val exception =
        shouldThrow<IllegalArgumentException> {
          Password.of(shortPassword)
        }
      exception.message shouldBe "비밀번호는 최소 8글자 이상이어야 합니다."
    }

    test("이미 인코딩된 비밀번호로 객체 생성 성공") {
      // given
      val encodedPassword = "encoded_password_value"

      // when
      val password = Password.fromEncoded(encodedPassword)

      // then
      password.encodedValue shouldBe encodedPassword
    }

    test("비밀번호 일치 여부 확인 성공") {
      // given
      val rawPassword = "password1234"
      val password = Password.of(rawPassword)

      // when & then
      password.matches(rawPassword) shouldBe true
      password.matches("wrongpassword") shouldBe false
    }

    test("같은 원본 비밀번호는 항상 같은 인코딩 값을 가진다") {
      // given
      val rawPassword = "password1234"

      // when
      val password1 = Password.of(rawPassword)
      val password2 = Password.of(rawPassword)

      // then
      password1.encodedValue shouldBe password2.encodedValue
    }
  })
