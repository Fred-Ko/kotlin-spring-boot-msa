package com.restaurant.application.user.command.handler

import com.restaurant.application.user.TestConfig
import com.restaurant.application.user.command.RegisterUserCommand
import com.restaurant.application.user.common.UserErrorCode
import com.restaurant.domain.user.aggregate.User
import com.restaurant.domain.user.repository.UserRepository
import com.restaurant.domain.user.vo.Email
import com.restaurant.domain.user.vo.Name
import com.restaurant.domain.user.vo.Password
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeEmpty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [TestConfig::class])
@Transactional // 테스트 후 자동으로 트랜잭션 롤백
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class RegisterUserCommandHandlerTest(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val handler: RegisterUserCommandHandler,
) : BehaviorSpec({

        extensions(SpringExtension)

        Given("유효한 사용자 정보로 회원가입 요청을 보낼 때") {
            val command =
                RegisterUserCommand(
                    email = "test@example.com",
                    password = "password123",
                    name = "테스트유저",
                )

            When("회원가입 처리를 하면") {
                val result = handler.handle(command)

                Then("성공 결과가 반환되어야 한다") {
                    result.success shouldBe true
                    result.correlationId.shouldNotBeEmpty()
                }

                Then("사용자가 저장되어야 한다") {
                    val savedUser = userRepository.findByEmail(Email(command.email))
                    savedUser shouldNotBe null
                    savedUser?.email?.value shouldBe command.email
                    savedUser?.name?.value shouldBe command.name
                }
            }
        }

        Given("이미 가입된 이메일로 회원가입 요청을 보낼 때") {
            // 기존 사용자 생성
            val existingEmail = "existing@example.com"
            userRepository.save(
                User.create(
                    email = Email(existingEmail),
                    password =
                        Password
                            .of("password123"),
                    name = Name.of("기존사용자"),
                ),
            )

            val command =
                RegisterUserCommand(
                    email = existingEmail,
                    password = "password123",
                    name = "테스트유저",
                )

            When("회원가입 처리를 하면") {
                val result = handler.handle(command)

                Then("이메일 중복 오류가 반환되어야 한다") {
                    result.success shouldBe false
                    result.errorCode shouldBe UserErrorCode.DUPLICATE_EMAIL.code
                }
            }
        }

        Given("유효하지 않은 이메일 형식으로 회원가입 요청을 보낼 때") {
            val command =
                RegisterUserCommand(
                    email = "invalid-email",
                    password = "password123",
                    name = "테스트유저",
                )

            When("회원가입 처리를 하면") {
                val result = handler.handle(command)

                Then("입력 유효성 오류가 반환되어야 한다") {
                    result.success shouldBe false
                    result.errorCode shouldBe UserErrorCode.INVALID_INPUT.code
                }
            }
        }
    })
