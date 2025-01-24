domains:
  user:
    adapter:
      src/main/kotlin/com/company/user/adapter:
        inbound:
          controller:
            command:
              - UserCommandController.kt
            query:
              - UserQueryController.kt
          dto:
            request:
              - UserRequest.kt
            response:
              - UserResponse.kt
          config:
            - WebSecurityConfig.kt
        outbound:
          persistence:
            - UserJpaRepository.kt
            entity:
              - UserEntity.kt
          messaging:
            - KafkaEventPublisher.kt
          config:
            - JpaConfig.kt
            - KafkaConfig.kt
          repository:
            - UserRepositoryImpl.kt

    core:
      application:
        src/main/kotlin/com/company/user/application:
          command:
            handler:
              - UserHandler.kt
            service:
              - UserCommandService.kt
            dto:
              - UserCommandDto.kt
          query:
            handler:
              - GetUserHandler.kt
            service:
              - UserQueryService.kt
            dto:
              - UserQueryDto.kt
          port:
            - UserRepositoryPort.kt
            - EventPublisherPort.kt
      domain:
        src/main/kotlin/com/company/user/domain:
          model:
            - User.kt
            - UserId.kt
          vo:
            - Email.kt
          event:
            - UserCreatedEvent.kt
          exception:
            - UserValidationException.kt

libraries:
  outbox:
    src/main/kotlin/com/company/outbox:
      config:
        - OutboxAutoConfiguration.kt
        - OutboxProperties.kt
      domain:
        - OutboxEvent.kt
      scheduler:
        - OutboxScheduler.kt
      repository:
        - OutboxEventRepository.kt
  kafka-commons:
    src/main/kotlin/com/company/kafka:
      config:
        - KafkaProducerConfig.kt
        - KafkaConsumerConfig.kt
      serializer:
        - AvroSerializer.kt
      error:
        - DeadLetterProducer.kt

shared:
  shared-common:
    src/main/kotlin/com/company/common:
      exception:
        - GlobalExceptionHandler.kt
      util:
        - DateTimeUtils.kt
  shared-events:
    src/main/kotlin/com/company/events/user:
      - UserCreatedEvent.kt
  shared-utils:
    src/main/kotlin/com/company/utils:
      extensions:
        - StringExtensions.kt
      coroutine:
        - CoroutineDispatcherProvider.kt
