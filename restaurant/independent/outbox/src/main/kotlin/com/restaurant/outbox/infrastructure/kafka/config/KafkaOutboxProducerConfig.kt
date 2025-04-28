package com.restaurant.outbox.infrastructure.kafka.config

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
 * Configures KafkaTemplate to send raw byte arrays (Rule 129).
 */
@Configuration
class KafkaOutboxProducerConfig {
    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    // No Schema Registry URL needed here as we send raw bytes
    // @Value("\${spring.kafka.properties.schema.registry.url}")
    // private lateinit var schemaRegistryUrl: String

    @Bean("outboxKafkaProducerFactory")
    fun outboxProducerFactory(): ProducerFactory<String, ByteArray> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        // Rule 129: Use ByteArraySerializer for value as payload is pre-serialized bytes
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = ByteArraySerializer::class.java

        // Optional: Configure idempotence, retries, acks etc. for higher reliability
        // configProps[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = "true"
        // configProps[ProducerConfig.ACKS_CONFIG] = "all"
        // configProps[ProducerConfig.RETRIES_CONFIG] = Int.MAX_VALUE.toString()
        // configProps[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = "5"

        // No Schema Registry config needed for ByteArraySerializer
        // configProps["schema.registry.url"] = schemaRegistryUrl

        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean("outboxKafkaTemplate")
    fun outboxKafkaTemplate(): KafkaTemplate<String, ByteArray> = KafkaTemplate(outboxProducerFactory())
}
