package com.restaurant.payment.infrastructure.config

import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka

/**
 * Payment 도메인 Kafka Consumer 설정 (Rule VII.2.24)
 * @KafkaListener 어노테이션 기반 메시지 처리를 위한 설정
 */
@Configuration
@EnableKafka
class KafkaConsumerConfig
