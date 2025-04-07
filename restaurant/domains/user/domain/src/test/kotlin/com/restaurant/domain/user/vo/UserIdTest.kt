package com.restaurant.domain.user.vo

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class UserIdTest :
    FunSpec({

        test("UserId 생성 및 값 확인") {
            // given
            val id = 123L

            // when
            val userId = UserId.of(id)

            // then
            userId.value shouldBe id
        }

        test("toString()은 UserId의 값을 문자열로 반환한다") {
            // given
            val id = 123L
            val userId = UserId.of(id)

            // when
            val result = userId.toString()

            // then
            result shouldBe id.toString()
        }

        test("같은 값의 UserId는 동등하다") {
            // given
            val userId1 = UserId.of(123L)
            val userId2 = UserId.of(123L)
            val userId3 = UserId.of(456L)

            // then
            userId1 shouldBe userId2
            userId1 shouldNotBe userId3
        }
    })
