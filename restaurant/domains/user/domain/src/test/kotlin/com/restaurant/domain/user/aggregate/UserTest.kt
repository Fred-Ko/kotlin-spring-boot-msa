package com.restaurant.domain.user.aggregate

import com.restaurant.domain.user.entity.Address
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.Name
import com.restaurant.domain.user.vo.Password
import com.restaurant.domain.user.vo.UserId
import io.kotest.assertions.throwables.shouldThrow
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
            val name = Name.of("테스트유저")

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
            val name = Name.of("테스트유저")
            val createdAt = LocalDateTime.now().minusDays(1)
            val updatedAt = LocalDateTime.now()
            val addresses = emptyList<Address>()

            // when
            val user = User.reconstitute(id, email, password, name, addresses, createdAt, updatedAt)

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
                    Name.of("원래이름"),
                )
            val newName = Name.of("변경된이름")

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
                    Name.of("테스트유저"),
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
                    Name.of("테스트유저"),
                )

            // when & then
            user.checkPassword(rawPassword) shouldBe true
            user.checkPassword("wrongpassword") shouldBe false
        }

        // 실패 케이스 추가
        test("빈 이름으로 사용자 생성 실패") {
            // given
            val email = Email("test@example.com")
            val password = Password.of("password123")
            val emptyName = ""

            // when & then
            shouldThrow<IllegalArgumentException> { Name.of(emptyName) }
        }

        test("너무 짧은 비밀번호로 변경 실패") {
            // given
            val user =
                User.create(
                    Email("test@example.com"),
                    Password.of("validpassword123"),
                    Name.of("테스트유저"),
                )
            val tooShortPassword = "123" // 최소 길이보다 짧은 비밀번호

            // when & then
            shouldThrow<IllegalArgumentException> { user.changePassword(tooShortPassword) }
        }
    })
