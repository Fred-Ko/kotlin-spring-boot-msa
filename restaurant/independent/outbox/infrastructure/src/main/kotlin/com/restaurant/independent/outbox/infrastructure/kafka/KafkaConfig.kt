package com.restaurant.independent.outbox.infrastructure.kafka

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
 * Outbox 모듈의 Kafka 설정을 제공하는 설정 클래스.
 */
@Configuration
class KafkaConfig(
    @Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String,
    @Value("\${spring.kafka.producer.client-id}")
    private val clientId: String,
    @Value("\${spring.kafka.producer.acks}")
    private val acks: String,
    @Value("\${spring.kafka.producer.retries}")
    private val retries: Int,
    @Value("\${spring.kafka.producer.batch-size}")
    private val batchSize: Int,
    @Value("\${spring.kafka.producer.buffer-memory}")
    private val bufferMemory: Long,
    @Value("\${spring.kafka.producer.compression-type}")
    private val compressionType: String,
    @Value("\${spring.kafka.producer.max-in-flight-requests-per-connection}")
    private val maxInFlightRequestsPerConnection: Int,
    @Value("\${spring.kafka.producer.max-request-size}")
    private val maxRequestSize: Int,
    @Value("\${spring.kafka.producer.request-timeout-ms}")
    private val requestTimeoutMs: Int,
    @Value("\${spring.kafka.producer.enable-idempotence}")
    private val enableIdempotence: Boolean,
    @Value("\${spring.kafka.producer.properties.schema.registry.url:http://localhost:8081}")
    private val schemaRegistryUrl: String,
) {
    /**
     * Kafka Producer 설정을 생성합니다.
     */
    @Bean
    fun producerFactory(): ProducerFactory<String, ByteArray> {
        val configProps =
            mutableMapOf<String, Any>(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ProducerConfig.CLIENT_ID_CONFIG to clientId,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to ByteArraySerializer::class.java,
                ProducerConfig.ACKS_CONFIG to acks,
                ProducerConfig.RETRIES_CONFIG to retries,
                ProducerConfig.BATCH_SIZE_CONFIG to batchSize,
                ProducerConfig.BUFFER_MEMORY_CONFIG to bufferMemory,
                ProducerConfig.COMPRESSION_TYPE_CONFIG to compressionType,
                ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION to maxInFlightRequestsPerConnection,
                ProducerConfig.MAX_REQUEST_SIZE_CONFIG to maxRequestSize,
                ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG to requestTimeoutMs,
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to enableIdempotence,
                ProducerConfig.TRANSACTION_TIMEOUT_CONFIG to 900000, // 15 minutes
                ProducerConfig.TRANSACTIONAL_ID_CONFIG to "outbox-tx-",
                "schema.registry.url" to schemaRegistryUrl,
            )

        return DefaultKafkaProducerFactory(configProps)
    }

    /**
     * KafkaTemplate을 생성합니다.
     */
    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, ByteArray> = KafkaTemplate(producerFactory())
}
