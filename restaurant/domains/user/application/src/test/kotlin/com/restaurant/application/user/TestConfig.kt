package com.restaurant.application.user

import com.restaurant.application.user.command.handler.*
import com.restaurant.domain.user.repository.UserRepository
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@TestConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = ["com.restaurant.application.user", "com.restaurant.infrastructure.user", "com.restaurant.domain.user"])
@EnableJpaRepositories(basePackages = ["com.restaurant.infrastructure.user.repository"])
@EntityScan(basePackages = ["com.restaurant.infrastructure.user.entity"])
class TestConfig {

    @Bean
    fun registerUserCommandHandler(userRepository: UserRepository): RegisterUserCommandHandler {
        return RegisterUserCommandHandler(userRepository)
    }

    @Bean
    fun loginCommandHandler(userRepository: UserRepository): LoginCommandHandler {
        return LoginCommandHandler(userRepository)
    }

    @Bean
    fun changePasswordCommandHandler(userRepository: UserRepository): ChangePasswordCommandHandler {
        return ChangePasswordCommandHandler(userRepository)
    }

    @Bean
    fun deleteUserCommandHandler(userRepository: UserRepository): DeleteUserCommandHandler {
        return DeleteUserCommandHandler(userRepository)
    }

    @Bean
    fun updateProfileCommandHandler(userRepository: UserRepository): UpdateProfileCommandHandler {
        return UpdateProfileCommandHandler(userRepository)
    }
}