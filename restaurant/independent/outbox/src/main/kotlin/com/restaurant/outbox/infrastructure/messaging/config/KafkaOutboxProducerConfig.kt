package com.restaurant.outbox.infrastructure.messaging.config

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

/**
 * Kafka Producer Configuration specifically for the Outbox module.
 * Rule 80: 독립 모듈의 Infrastructure 레이어 내 messaging/config 패키지에 위치
 * Rule VII.1: Avro4k 방법론을 지원하는 Kafka 설정
 * Rule VII.2.17: Spring Kafka 설정 규칙 준수
 */
@Configuration
open class KafkaOutboxProducerConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String,
    @Value("\${spring.kafka.schema-registry-url:http://localhost:8081}")
    private val schemaRegistryUrl: String,
) {
    /**
     * Outbox 모듈용 Kafka Producer Factory
     * Rule 80: ByteArray 페이로드를 처리하는 범용적인 Producer
     * Rule VII.1: Avro 바이너리 데이터 전송을 위한 설정
     */
    @Bean
    open fun outboxProducerFactory(): ProducerFactory<String, ByteArray> {
        val configProps =
            mapOf(
                // Rule VII.2.18: Bootstrap Servers 설정
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                // Rule VII.2.19: Key Serializer 설정
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                // Rule 80: ByteArray 직렬화 (Avro 바이너리 페이로드 지원)
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to ByteArraySerializer::class.java,
                // 안정성을 위한 설정
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to true,
                ProducerConfig.ACKS_CONFIG to "all",
                ProducerConfig.RETRIES_CONFIG to 3,
                ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION to 1,
                // 성능 최적화 설정
                ProducerConfig.BATCH_SIZE_CONFIG to 16384,
                ProducerConfig.LINGER_MS_CONFIG to 5,
                ProducerConfig.BUFFER_MEMORY_CONFIG to 33554432,
                ProducerConfig.COMPRESSION_TYPE_CONFIG to "lz4",
                // 타임아웃 설정
                ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG to 120000,
                ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG to 30000,
                ProducerConfig.MAX_BLOCK_MS_CONFIG to 60000,
                // 토픽 자동 생성 설정 - 발행 시 토픽이 없으면 자동 생성
                "allow.auto.create.topics" to true,
            )
        return DefaultKafkaProducerFactory(configProps)
    }

    /**
     * Outbox 모듈용 Kafka Template
     * Rule 80: 독립 모듈의 메시지 브로커 전송 컴포넌트에서 사용
     */
    @Bean
    open fun outboxKafkaTemplate(outboxProducerFactory: ProducerFactory<String, ByteArray>): KafkaTemplate<String, ByteArray> =
        KafkaTemplate(outboxProducerFactory)
}
