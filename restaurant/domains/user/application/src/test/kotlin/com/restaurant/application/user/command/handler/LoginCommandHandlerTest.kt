package com.restaurant.application.user.command.handler

import com.restaurant.application.user.TestConfig
import com.restaurant.application.user.command.LoginCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.application.user.exception.UserApplicationException
import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.Name
import com.restaurant.domain.user.vo.Password
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [TestConfig::class])
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class LoginCommandHandlerTest(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val handler: LoginCommandHandler,
) : BehaviorSpec({

        extensions(SpringExtension)

        Given("올바른 이메일과 비밀번호로 로그인을 시도할 때") {
            val userEmail = "test@example.com"
            val userPassword = "password123"
            val command = LoginCommand(email = userEmail, password = userPassword)

            // 테스트 사용자 생성
            val user =
                userRepository.save(
                    User.create(
                        email = Email.of(userEmail),
                        password = Password.of(userPassword),
                        name = Name.of("테스트유저"),
                    ),
                )

            When("로그인 처리를 하면") {
                val result = handler.handle(command)

                Then("결과는 성공한 사용자 ID를 반환해야 한다") {
                    result shouldBe user.id!!.value
                }
            }
        }

        Given("가입되지 않은 이메일로 로그인을 시도할 때") {
            val nonExistentEmail = "nonexistent@example.com"
            val command = LoginCommand(email = nonExistentEmail, password = "anypassword")

            When("로그인 처리를 하면") {
                Then("인증 실패 예외가 발생해야 한다") {
                    val exception =
                        shouldThrow<UserApplicationException.Authentication.InvalidCredentials> {
                            handler.handle(command)
                        }

                    exception.errorCode shouldBe UserErrorCode.INVALID_CREDENTIALS
                }
            }
        }

        Given("올바르지 않은 비밀번호로 로그인을 시도할 때") {
            val userEmail = "test2@example.com"
            val actualPassword = "actualPassword"
            val wrongPassword = "wrongPassword"
            val command = LoginCommand(email = userEmail, password = wrongPassword)

            // 테스트 사용자 생성
            userRepository.save(
                User.create(
                    email = Email.of(userEmail),
                    password = Password.of(actualPassword),
                    name = Name.of("테스트유저2"),
                ),
            )

            When("로그인 처리를 하면") {
                Then("인증 실패 예외가 발생해야 한다") {
                    val exception =
                        shouldThrow<UserApplicationException.Authentication.InvalidCredentials> {
                            handler.handle(command)
                        }

                    exception.errorCode shouldBe UserErrorCode.INVALID_CREDENTIALS
                }
            }
        }

        Given("유효하지 않은 이메일 형식으로 로그인 요청을 보낼 때") {
            val command = LoginCommand(email = "invalid-email", password = "password123")

            When("로그인 처리를 하면") {
                Then("입력 유효성 예외가 발생해야 한다") {
                    val exception =
                        shouldThrow<UserApplicationException.Authentication.InvalidInput> {
                            handler.handle(command)
                        }

                    exception.errorCode shouldBe UserErrorCode.INVALID_INPUT
                }
            }
        }
    })
