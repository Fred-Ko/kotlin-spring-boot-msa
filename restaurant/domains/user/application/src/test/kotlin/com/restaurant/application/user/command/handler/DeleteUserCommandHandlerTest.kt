package com.restaurant.application.user.handler

import com.restaurant.user.application.TestConfig
import com.restaurant.user.application.common.UserErrorCode
import com.restaurant.user.application.dto.command.DeleteUserCommand
import com.restaurant.user.application.exception.UserApplicationException
import com.restaurant.user.domain.aggregate.User
import com.restaurant.user.domain.repository.UserRepository
import com.restaurant.user.domain.vo.Email
import com.restaurant.user.domain.vo.Name
import com.restaurant.user.domain.vo.Password
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [TestConfig::class])
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DeleteUserCommandHandlerTest(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val handler: DeleteUserCommandHandler,
    @Autowired private val applicationContext: ApplicationContext,
) : BehaviorSpec({

        extensions(SpringExtension)

        Given("올바른 비밀번호로 계정 삭제 요청을 보낼 때") {
            val password = "password123"

            // 테스트 사용자 생성
            val savedUser =
                userRepository.save(
                    User.create(
                        email = Email.of("test@example.com"),
                        password = Password.of(password),
                        name = Name.of("테스트유저"),
                    ),
                )

            val command =
                DeleteUserCommand(
                    userId = savedUser.id!!.value,
                    password = password,
                )

            When("계정 삭제 처리를 하면") {
                handler.handle(command)

                Then("사용자 계정이 삭제되어야 한다") {
                    userRepository.findById(savedUser.id!!) shouldBe null
                }
            }
        }

        Given("존재하지 않는 사용자 ID로 계정 삭제 요청을 보낼 때") {
            val nonExistentUserId = 999L
            val command =
                DeleteUserCommand(
                    userId = nonExistentUserId,
                    password = "anypassword",
                )

            When("계정 삭제 처리를 하면") {
                Then("사용자를 찾을 수 없음 예외가 발생해야 한다") {
                    val exception =
                        shouldThrow<UserApplicationException.Query.NotFound> {
                            handler.handle(command)
                        }

                    exception.errorCode shouldBe UserErrorCode.NOT_FOUND
                }
            }
        }

        Given("잘못된 비밀번호로 계정 삭제 요청을 보낼 때") {
            val actualPassword = "actualPassword123"
            val wrongPassword = "wrongPassword123"

            // 테스트 사용자 생성
            val savedUser =
                userRepository.save(
                    User.create(
                        email = Email.of("test@example.com"),
                        password = Password.of(actualPassword),
                        name = Name.of("테스트유저2"),
                    ),
                )

            val command =
                DeleteUserCommand(
                    userId = savedUser.id!!.value,
                    password = wrongPassword,
                )

            When("계정 삭제 처리를 하면") {
                Then("비밀번호 불일치 예외가 발생해야 한다") {
                    val exception =
                        shouldThrow<UserApplicationException.Password.InvalidPassword> {
                            handler.handle(command)
                        }

                    exception.errorCode shouldBe UserErrorCode.INVALID_PASSWORD
                }
            }
        }
    })
