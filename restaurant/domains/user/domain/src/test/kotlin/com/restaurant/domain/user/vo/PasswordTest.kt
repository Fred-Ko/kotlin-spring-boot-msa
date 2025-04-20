package com.restaurant.domain.user.vo

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PasswordTest :
    FunSpec({

        test("최소 길이보다 짧은 비밀번호 유효성 검사 시 예외 발생") {
            // given
            val shortPassword = "123"

            // then
            val exception =
                shouldThrow<IllegalArgumentException> {
                    Password.validateRawPassword(shortPassword)
                }
            exception.message shouldBe "비밀번호는 최소 8글자 이상이어야 합니다."
        }

        test("최소 길이 만족하는 비밀번호 유효성 검사 성공") {
            // given
            val validPassword = "password1234"

            // when & then (예외가 발생하지 않아야 함)
            Password.validateRawPassword(validPassword)
        }

        test("이미 인코딩된 비밀번호로 객체 생성 성공") {
            // given
            val encodedPassword = "encoded_password_value"

            // when
            val password = Password.fromEncoded(encodedPassword)

            // then
            password.encodedValue shouldBe encodedPassword
        }

        test("빈 인코딩된 비밀번호로 객체 생성 시 예외 발생") {
            // given
            val emptyEncodedPassword = ""

            // then
            val exception =
                shouldThrow<IllegalArgumentException> {
                    Password.fromEncoded(emptyEncodedPassword)
                }
            exception.message shouldBe "인코딩된 비밀번호 값은 비어있을 수 없습니다."
        }
    })
