package com.restaurant.outbox.infrastructure.config

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

/**
 * Outbox 모듈의 Kafka 설정
 * Rule 88: KafkaTemplate Bean 정의
 * Rule VII.2.17: spring.kafka 네임스페이스를 통한 Kafka 설정
 */
@Configuration
open class OutboxKafkaConfig {
    /**
     * KafkaTemplate for Outbox message sending
     * Rule 88: OutboxMessageSender에서 사용할 KafkaTemplate Bean
     * Rule VII.1.3.4: JSON Schema 지원을 위한 Serializer 설정
     */
    @Bean
    open fun outboxKafkaTemplate(outboxProducerFactory: ProducerFactory<String, String>): KafkaTemplate<String, String> =
        KafkaTemplate(outboxProducerFactory)

    /**
     * Producer Factory for Outbox
     * Rule VII.1.3.4: JSON Schema Serializer를 사용한 Producer 설정
     * Rule VII.2.17: application.yml의 spring.kafka 설정을 따름
     */
    @Bean
    open fun outboxProducerFactory(): ProducerFactory<String, String> {
        val props = mutableMapOf<String, Any>()

        // 기본 설정 - application.yml에서 오버라이드 가능
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java

        // JSON Schema Serializer 설정 (Rule VII.1.3.4)
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = "io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer"

        // Schema Registry 설정 (Rule VII.1.3.4)
        props["schema.registry.url"] = "http://localhost:8081"
        props["auto.register.schemas"] = "false" // 스키마는 Gradle 플러그인으로 미리 등록
        props["use.latest.version"] = "true"

        // Producer 최적화 설정
        props[ProducerConfig.ACKS_CONFIG] = "all"
        props[ProducerConfig.RETRIES_CONFIG] = 3
        props[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true

        return DefaultKafkaProducerFactory(props)
    }
}
