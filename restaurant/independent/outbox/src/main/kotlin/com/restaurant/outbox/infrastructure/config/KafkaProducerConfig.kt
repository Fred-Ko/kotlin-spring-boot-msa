package com.restaurant.outbox.infrastructure.config

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
open class KafkaProducerConfig {
    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Value("\${spring.kafka.producer.properties.acks:all}")
    private lateinit var producerAcks: String

    @Bean
    open fun stringProducerFactory(): ProducerFactory<String, String> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.ACKS_CONFIG] = producerAcks
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    open fun stringKafkaTemplate(): KafkaTemplate<String, String> = KafkaTemplate(stringProducerFactory())
}
