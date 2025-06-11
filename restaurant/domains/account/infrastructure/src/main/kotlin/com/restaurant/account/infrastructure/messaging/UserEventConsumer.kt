package com.restaurant.account.infrastructure.messaging

import com.restaurant.account.application.command.dto.CreateAccountCommand
import com.restaurant.account.application.command.dto.DeactivateAccountCommand
import com.restaurant.account.application.command.usecase.CreateAccountUseCase
import com.restaurant.account.application.command.usecase.DeactivateAccountUseCase
import com.restaurant.account.domain.vo.UserId
import com.restaurant.account.infrastructure.messaging.event.UserEventCreated
import com.restaurant.account.infrastructure.messaging.event.UserEventDeactivated
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserEventConsumer(
    private val createAccountUseCase: CreateAccountUseCase,
    private val deactivateAccountUseCase: DeactivateAccountUseCase,
) {
    private val logger = KotlinLogging.logger {}

    @KafkaListener(topics = ["dev.restaurant.user.event.created.v1"])
    fun handleUserCreated(message: String) {
        try {
            logger.info { "Received UserCreated event: $message" }

            val event = Json.decodeFromString<UserEventCreated>(message)
            val command =
                CreateAccountCommand(
                    userId = UserId.of(UUID.fromString(event.id)),
                )

            createAccountUseCase.createAccount(command)
            logger.info { "Successfully created account for user: ${event.id}" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to handle UserCreated event: $message" }
            throw e
        }
    }

    @KafkaListener(topics = ["dev.restaurant.user.event.deactivated.v1"])
    fun handleUserDeactivated(message: String) {
        try {
            logger.info { "Received UserDeactivated event: $message" }

            val event = Json.decodeFromString<UserEventDeactivated>(message)
            val command =
                DeactivateAccountCommand(
                    userId = UserId.of(UUID.fromString(event.id)),
                )

            deactivateAccountUseCase.deactivateAccount(command)
            logger.info { "Successfully deactivated account for user: ${event.id}" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to handle UserDeactivated event: $message" }
            throw e
        }
    }
}
