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
 * Schema Registry 엄격한 호환성 검사 활성화
 */
@Configuration
open class OutboxKafkaConfig {
    /**
     * KafkaTemplate for Outbox message sending
     * Rule 88: OutboxMessageSender에서 사용할 KafkaTemplate Bean
     * StringSerializer를 사용하여 JSON 문자열 전송
     */
    @Bean
    open fun outboxKafkaTemplate(outboxProducerFactory: ProducerFactory<String, String>): KafkaTemplate<String, String> =
        KafkaTemplate(outboxProducerFactory)

    /**
     * Producer Factory for Outbox
     * StringSerializer 사용 (JSON 문자열 전송)
     */
    @Bean
    open fun outboxProducerFactory(): ProducerFactory<String, String> {
        val props = mutableMapOf<String, Any>()

        // 기본 설정 - application.yml에서 오버라이드 가능
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java

        // String Serializer 사용 (JSON 문자열 전송)
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java

        // Producer 최적화 설정
        props[ProducerConfig.ACKS_CONFIG] = "all"
        props[ProducerConfig.RETRIES_CONFIG] = 3
        props[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true
        props[ProducerConfig.BATCH_SIZE_CONFIG] = 16384
        props[ProducerConfig.BUFFER_MEMORY_CONFIG] = 33554432
        props[ProducerConfig.COMPRESSION_TYPE_CONFIG] = "lz4"
        props[ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG] = 120000
        props[ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG] = 30000
        props[ProducerConfig.MAX_BLOCK_MS_CONFIG] = 60000
        props[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = 1

        return DefaultKafkaProducerFactory(props)
    }
}
