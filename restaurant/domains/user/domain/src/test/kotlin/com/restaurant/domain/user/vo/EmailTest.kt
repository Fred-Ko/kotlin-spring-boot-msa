package com.restaurant.domain.user.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class EmailTest :
  FunSpec({

    test("유효한 이메일 형식으로 객체 생성 성공") {
      // given
      val validEmail = "test@example.com"

      // when
      val email = Email(validEmail)

      // then
      email.value shouldBe validEmail
    }

    test("유효하지 않은 이메일 형식으로 객체 생성 시 예외 발생") {
      val invalidEmails =
        listOf(
          "test",
          "test@",
          "@example.com",
          "test@example.",
          "",
        )

      invalidEmails.forEach { invalidEmail ->
        val exception =
          shouldThrow<IllegalArgumentException> {
            Email(invalidEmail)
          }
        exception.message shouldBe "유효한 이메일 형식이 아닙니다."
      }
    }

    test("toString()은 이메일 값을 반환한다") {
      // given
      val emailValue = "test@example.com"
      val email = Email(emailValue)

      // when
      val result = email.toString()

      // then
      result shouldBe emailValue
    }
  })
