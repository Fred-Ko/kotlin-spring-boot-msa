package com.restaurant.application.user

import com.restaurant.user.application.usecase.ChangePasswordCommandHandler
import com.restaurant.user.application.usecase.DeleteUserCommandHandler
import com.restaurant.user.application.usecase.GetUserProfileQueryHandler
import com.restaurant.user.application.usecase.LoginCommandHandler
import com.restaurant.user.application.usecase.RegisterUserCommandHandler
import com.restaurant.user.application.usecase.UpdateProfileCommandHandler
import com.restaurant.user.domain.repository.UserRepository
import org.mockito.Mockito
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootApplication
@ComponentScan(
    basePackages = ["com.restaurant.application.user"],
)
class TestConfig {
    @Bean
    fun userRepository(): UserRepository = Mockito.mock(UserRepository::class.java)

    @Bean
    fun passwordEncoder(): PasswordEncoder = Mockito.mock(PasswordEncoder::class.java)

    @Bean
    fun registerUserCommandHandler(
        userRepository: UserRepository,
        passwordEncoder: PasswordEncoder,
    ): RegisterUserCommandHandler = RegisterUserCommandHandler(userRepository, passwordEncoder)

    @Bean
    fun loginCommandHandler(
        userRepository: UserRepository,
        passwordEncoder: PasswordEncoder,
    ): LoginCommandHandler = LoginCommandHandler(userRepository, passwordEncoder)

    @Bean
    fun changePasswordCommandHandler(
        userRepository: UserRepository,
        passwordEncoder: PasswordEncoder,
    ): ChangePasswordCommandHandler = ChangePasswordCommandHandler(userRepository, passwordEncoder)

    @Bean
    fun deleteUserCommandHandler(
        userRepository: UserRepository,
        passwordEncoder: PasswordEncoder,
    ): DeleteUserCommandHandler = DeleteUserCommandHandler(userRepository, passwordEncoder)

    @Bean
    fun updateProfileCommandHandler(userRepository: UserRepository): UpdateProfileCommandHandler =
        UpdateProfileCommandHandler(userRepository)

    @Bean
    fun getUserProfileQueryHandler(userRepository: UserRepository): GetUserProfileQueryHandler = GetUserProfileQueryHandler(userRepository)
}
