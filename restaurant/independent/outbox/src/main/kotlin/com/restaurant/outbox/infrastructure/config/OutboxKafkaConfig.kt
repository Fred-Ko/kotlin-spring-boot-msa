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
     * Schema Registry 엄격한 호환성 검사를 통한 JSON 직렬화 (객체 직접 전송)
     */
    @Bean
    open fun outboxKafkaTemplate(outboxProducerFactory: ProducerFactory<String, Any>): KafkaTemplate<String, Any> =
        KafkaTemplate(outboxProducerFactory)

    /**
     * Producer Factory for Outbox
     * KafkaJsonSchemaSerializer + Schema Registry 엄격한 호환성 검사 (객체 직접 전송)
     */
    @Bean
    open fun outboxProducerFactory(): ProducerFactory<String, Any> {
        val props = mutableMapOf<String, Any>()

        // 기본 설정 - application.yml에서 오버라이드 가능
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java

        // JSON Schema Serializer 사용 (엄격한 호환성 검사)
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = "io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer"

        // Schema Registry 설정 (엄격한 호환성 검사)
        props["schema.registry.url"] = "http://localhost:8081"
        props["auto.register.schemas"] = "false" // 스키마는 미리 등록된 것만 사용
        props["use.latest.version"] = "true"

        // 엄격한 호환성 검사 비활성화
        props["latest.compatibility.strict"] = "false" // 엄격한 호환성 검사 비활성화
        props["normalize.schemas"] = "true" // 스키마 정규화
        props["json.fail.invalid.schema"] = "true" // 스키마 검증 실패 시 예외 발생 (주의: strict=false와 함께 사용 시 동작 확인 필요)
        props["json.write.dates.iso8601"] = "true" // ISO-8601 날짜 형식

        // Subject naming strategy
        props["value.subject.name.strategy"] = "io.confluent.kafka.serializers.subject.TopicNameStrategy"

        // JSON Schema 특화 설정
        props["json.oneof.for.nullables"] = "true" // nullable 필드를 oneOf로 처리
        props["json.schema.spec.version"] = "DRAFT_7" // JSON Schema 버전 명시
        props["json.indent.output"] = "false" // 압축된 JSON 출력

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
