package com.restaurant.outbox.infrastructure.messaging

import com.restaurant.outbox.application.dto.model.OutboxMessage
import com.restaurant.outbox.infrastructure.exception.OutboxException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component

/**
 * Outbox 메시지 전송 컴포넌트
 * Rule 88: Outbox Message Sender는 Outbox Event Entity를 읽어와 메시지 브로커로 전송
 * Rule VII.1: Avro 바이너리 페이로드를 Kafka로 전송
 * Rule 80: 독립 모듈의 Infrastructure 레이어 내 messaging 패키지에 위치
 */
@Component
class OutboxMessageSender(
    @Qualifier("outboxKafkaTemplate")
    private val kafkaTemplate: KafkaTemplate<String, ByteArray>,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Outbox 메시지를 처리하고 Kafka로 전송합니다.
     * Rule 88: payload (Avro 바이너리)를 메시지 브로커 메시지 payload로 구성하여 전송
     * Rule VII.1: ByteArray 페이로드를 직접 Kafka로 전송 (Avro 바이너리 지원)
     * 
     * @param messageDto 전송할 Outbox 메시지
     * @throws OutboxException.KafkaSendFailedException Kafka 전송 실패 시
     */
    fun processAndSendMessage(messageDto: OutboxMessage) {
        try {
            val kafkaMessage = createKafkaMessage(messageDto)
            val sendResult = kafkaTemplate.send(kafkaMessage).get()

            logger.info(
                "Successfully sent message to Kafka. Topic: {}, Partition: {}, Offset: {}, MessageId: {}, AggregateId: {}",
                messageDto.topic,
                sendResult.recordMetadata.partition(),
                sendResult.recordMetadata.offset(),
                messageDto.id,
                messageDto.aggregateId
            )
        } catch (e: Exception) {
            logger.error(
                "Failed to send message to Kafka. MessageId: {}, Topic: {}, AggregateId: {}, Error: {}",
                messageDto.id,
                messageDto.topic,
                messageDto.aggregateId,
                e.message,
                e
            )
            throw OutboxException.KafkaSendFailedException(
                message = "Failed to send message to Kafka for OutboxMessage ID ${messageDto.id} (Topic: ${messageDto.topic}, AggregateId: ${messageDto.aggregateId}): ${e.message}",
                cause = e
            )
        }
    }

    /**
     * OutboxMessage를 Kafka 메시지로 변환합니다.
     * Rule 88: Outbox Event Entity에 저장된 정보를 메시지 헤더에 포함
     * Rule VII.2.6: 토픽명 및 헤더 정보 설정
     * 
     * @param messageDto 변환할 Outbox 메시지
     * @return Kafka 전송용 메시지
     */
    private fun createKafkaMessage(messageDto: OutboxMessage): Message<ByteArray> {
        return MessageBuilder
            .withPayload(messageDto.payload) // Rule 88: Avro 바이너리 페이로드 사용
            .setHeader(KafkaHeaders.TOPIC, messageDto.topic)
            .setHeader(KafkaHeaders.KEY, messageDto.aggregateId) // Rule 88: Aggregate ID를 메시지 키로 사용
            .also { builder ->
                // Rule 88: Outbox Event Entity에 저장된 헤더 정보 포함
                messageDto.headers.forEach { (key, value) ->
                    builder.setHeader(key, value)
                }
                
                // 추가 메타데이터 헤더
                builder.setHeader("outbox-message-id", messageDto.id?.toString() ?: "unknown")
                builder.setHeader("aggregate-type", messageDto.aggregateType)
                builder.setHeader("event-type", messageDto.eventType)
                builder.setHeader("created-at", messageDto.createdAt.toString())
            }
            .build()
    }
}
