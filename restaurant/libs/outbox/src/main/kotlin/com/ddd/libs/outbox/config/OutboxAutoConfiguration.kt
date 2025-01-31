package com.ddd.libs.outbox.config

import com.ddd.libs.outbox.implementation.KafkaOutboxEventPublisherImpl
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@EntityScan(basePackages = ["com.ddd.libs.outbox.entity"])
@EnableJpaRepositories(basePackages = ["com.ddd.libs.outbox.implementation"])
class OutboxAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun kafkaOutboxEventPublisher(
            kafkaTemplate: KafkaTemplate<String, String>,
            objectMapper: ObjectMapper
    ): KafkaOutboxEventPublisherImpl {
        return KafkaOutboxEventPublisherImpl(kafkaTemplate, objectMapper)
    }
}
