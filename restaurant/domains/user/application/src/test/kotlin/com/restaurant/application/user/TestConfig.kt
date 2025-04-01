package com.restaurant.application.user

import com.restaurant.application.user.command.handler.ChangePasswordCommandHandler
import com.restaurant.application.user.command.handler.DeleteUserCommandHandler
import com.restaurant.application.user.command.handler.LoginCommandHandler
import com.restaurant.application.user.command.handler.RegisterUserCommandHandler
import com.restaurant.application.user.command.handler.UpdateProfileCommandHandler
import com.restaurant.domain.user.repository.UserRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan
@ComponentScan(basePackages = ["com.restaurant.application.user", "com.restaurant.infrastructure.user", "com.restaurant.domain.user"])
@EnableJpaRepositories(basePackages = ["com.restaurant.infrastructure.user.repository"])
@EntityScan(basePackages = ["com.restaurant.infrastructure.user.entity"])
class TestConfig {
    @Bean
    fun registerUserCommandHandler(userRepository: UserRepository): RegisterUserCommandHandler = RegisterUserCommandHandler(userRepository)

    @Bean
    fun loginCommandHandler(userRepository: UserRepository): LoginCommandHandler = LoginCommandHandler(userRepository)

    @Bean
    fun changePasswordCommandHandler(userRepository: UserRepository): ChangePasswordCommandHandler =
        ChangePasswordCommandHandler(userRepository)

    @Bean
    fun deleteUserCommandHandler(userRepository: UserRepository): DeleteUserCommandHandler = DeleteUserCommandHandler(userRepository)

    @Bean
    fun updateProfileCommandHandler(userRepository: UserRepository): UpdateProfileCommandHandler =
        UpdateProfileCommandHandler(userRepository)
}
