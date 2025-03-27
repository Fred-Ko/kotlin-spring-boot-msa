package com.restaurant.domain.user.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NameTest :
  FunSpec({

    test("유효한 이름으로 객체 생성 성공") {
      // given
      val validName = "홍길동"

      // when
      val name = Name.of(validName)

      // then
      name.value shouldBe validName
    }

    test("빈 이름으로 객체 생성 시 예외 발생") {
      // given
      val invalidNames = listOf("", "   ", "\t", "\n")

      // when & then
      invalidNames.forEach { invalidName ->
        val exception =
          shouldThrow<IllegalArgumentException> {
            Name.of(invalidName)
          }
        exception.message shouldBe "이름은 공백일 수 없습니다."
      }
    }
  })
