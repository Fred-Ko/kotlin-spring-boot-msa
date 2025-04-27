package com.restaurant.domain.user.aggregate

import com.restaurant.user.domain.model.Address
import com.restaurant.user.domain.vo.Email
import com.restaurant.user.domain.vo.Name
import com.restaurant.user.domain.vo.Password
import com.restaurant.user.domain.vo.UserId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class UserTest :
    FunSpec({

        // 테스트용 인코딩된 비밀번호 (실제 인코딩 방식과 무관한 임의 값)
        val encodedPasswordValue = "encoded_password_123"
        val passwordVo = Password.fromEncoded(encodedPasswordValue)

        test("유효한 정보로 사용자 생성 성공") {
            // given
            val email = Email.of("test@example.com")
            val name = Name.of("테스트유저")

            // when
            val (user, events) = User.create(email, passwordVo, name)

            // then
            user.id.shouldBeNull()
            user.email shouldBe email
            user.password shouldBe passwordVo // 인코딩된 VO 확인
            user.name shouldBe name
            user.createdAt.shouldNotBeNull()
            user.updatedAt.shouldNotBeNull()
            events.size shouldBe 1 // 이벤트 발생 확인 (선택적)
        }

        test("기존 정보로 사용자 복원 성공") {
            // given
            val id = UserId.of(1L)
            val email = Email.of("test@example.com")
            val name = Name.of("테스트유저")
            val createdAt = LocalDateTime.now().minusDays(1)
            val updatedAt = LocalDateTime.now()
            val addresses = emptyList<Address>() // model.Address 사용

            // when
            val user = User.reconstitute(id, email, passwordVo, name, addresses, createdAt, updatedAt)

            // then
            user.id shouldBe id
            user.email shouldBe email
            user.password shouldBe passwordVo
            user.name shouldBe name
            user.createdAt shouldBe createdAt
            user.updatedAt shouldBe updatedAt
        }

        test("사용자 프로필 업데이트 성공") {
            // given
            val (user, _) =
                User.create(
                    Email.of("test@example.com"),
                    passwordVo,
                    Name.of("원래이름"),
                )
            val newName = Name.of("변경된이름")

            // when
            val (updatedUser, events) = user.updateProfile(newName)

            // then
            updatedUser.name shouldBe newName
            updatedUser.email shouldBe user.email
            updatedUser.password shouldBe user.password
            updatedUser.createdAt shouldBe user.createdAt
            updatedUser.updatedAt shouldBeGreaterThanOrEqualTo user.updatedAt
            events.size shouldBe 1 // 이벤트 발생 확인 (선택적)
        }

        test("사용자 비밀번호 변경 성공") {
            // given
            val (user, _) =
                User.create(
                    Email.of("test@example.com"),
                    passwordVo,
                    Name.of("테스트유저"),
                )
            val newEncodedPasswordValue = "new_encoded_password_456"
            val newPasswordVo = Password.fromEncoded(newEncodedPasswordValue)

            // when
            val (updatedUser, events) = user.changePassword(newPasswordVo)

            // then
            updatedUser.password shouldBe newPasswordVo // 변경된 VO 확인
            updatedUser.email shouldBe user.email
            updatedUser.name shouldBe user.name
            updatedUser.createdAt shouldBe user.createdAt
            updatedUser.updatedAt shouldBeGreaterThanOrEqualTo user.updatedAt
            events.size shouldBe 1 // 이벤트 발생 확인 (선택적)
        }

        test("빈 이름으로 사용자 생성 실패") {
            // given
            val email = Email.of("test@example.com")
            // Password.of 제거로 passwordVo 직접 사용
            val emptyName = ""

            // when & then
            shouldThrow<IllegalArgumentException> { Name.of(emptyName) }
            // User.create 호출은 Name 생성에서 실패하므로 별도 테스트 불필요
        }

        test("너무 짧은 비밀번호 유효성 검사 실패") {
            // given
            val tooShortPassword = "123"

            // then
            shouldThrow<IllegalArgumentException> { Password.validateRawPassword(tooShortPassword) }
        }
    })
