package com.restaurant.outbox.infrastructure.messaging

import com.restaurant.outbox.application.dto.OutboxMessage
import com.restaurant.outbox.infrastructure.exception.OutboxException
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

/**
 * Outbox 메시지 전송 컴포넌트
 * Rule 88: KafkaTemplate을 사용하여 OutboxMessage를 Kafka로 전송
 * Rule VII.1.3.4: spring-kafka KafkaTemplate 기반 메시지 발행
 * Rule 80: 독립 모듈의 Infrastructure 레이어 내 messaging 패키지에 위치
 */
@Component
class OutboxMessageSender(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Outbox 메시지를 처리하고 Kafka로 전송합니다.
     * Rule 88: 객체를 KafkaTemplate을 통해 메시지 브로커로 전송
     * Rule VII.1.3.4: KafkaJsonSchemaSerializer를 통한 JSON 직렬화 및 Schema Registry 활용 (이제 StringSerializer 사용)
     * 
     * @param messageDto 전송할 Outbox 메시지
     * @throws OutboxException.KafkaSendFailedException Kafka 전송 실패 시
     */
    fun processAndSendMessage(messageDto: OutboxMessage) {
        try {
            val producerRecord = createProducerRecord(messageDto)

            // KafkaTemplate을 사용하여 비동기 전송
            val future: CompletableFuture<SendResult<String, String>> = kafkaTemplate.send(producerRecord)
            
            // 비동기 콜백 설정
            future.whenComplete { result, exception ->
                if (exception != null) {
                    logger.error(
                        "Failed to send message to Kafka. MessageId: {}, Topic: {}, AggregateId: {}, Error: {}",
                        messageDto.id,
                        messageDto.topic,
                        messageDto.aggregateId,
                        exception.message,
                        exception
                    )
                    throw OutboxException.KafkaSendFailedException(
                        message = "Failed to send message to Kafka for OutboxMessage ID ${messageDto.id} (Topic: ${messageDto.topic}, AggregateId: ${messageDto.aggregateId}): ${exception.message}",
                        cause = exception
                    )
                } else {
                    logger.info(
                        "Successfully sent message to Kafka. Topic: {}, Partition: {}, Offset: {}, MessageId: {}, AggregateId: {}",
                        result.recordMetadata.topic(),
                        result.recordMetadata.partition(),
                        result.recordMetadata.offset(),
                        messageDto.id,
                        messageDto.aggregateId
                    )
                }
            }

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
     * OutboxMessage를 Kafka ProducerRecord로 변환합니다.
     * Rule 88: Outbox Event Entity에 저장된 정보를 메시지 헤더에 포함
     * Rule VII.2.6: 토픽명 및 헤더 정보 설정
     * Rule VII.1.3.4: 객체 페이로드 및 Schema Registry 헤더 처리
     * 
     * @param messageDto 변환할 Outbox 메시지
     * @return Kafka 전송용 ProducerRecord
     */
    private fun createProducerRecord(messageDto: OutboxMessage): ProducerRecord<String, String> {
        // OutboxMessage.payload는 이미 kotlinx.serialization으로 직렬화된 JSON 문자열.
        // 이를 그대로 Kafka에 전송 (StringSerializer 사용).
        val payloadAsString: String = when (val p = messageDto.payload) {
            is String -> p
            is ByteArray -> {
                // ByteArray의 경우, UTF-8 문자열로 변환. (CI/CD에서 스키마 검증 시 이 변환을 고려해야 함)
                logger.warn(
                    "ByteArray payload received. Converting to UTF-8 string. OutboxMessage ID: {}",
                    messageDto.id
                )
                p.toString(Charsets.UTF_8)
            }
            else -> {
                logger.error(
                    "Unsupported payload type: {}. Expected String or ByteArray. OutboxMessage ID: {}",
                    p::class.simpleName, messageDto.id
                )
                throw OutboxException.KafkaSendFailedException(
                    message = "Unsupported payload type for OutboxMessage ID ${messageDto.id}: ${p::class.simpleName}"
                )
            }
        }

        val record = ProducerRecord<String, String>(
            messageDto.topic,
            messageDto.aggregateId, // Rule 88: Aggregate ID를 메시지 키로 사용
            payloadAsString // JSON 문자열 직접 전송
        )

        // Rule 88: Outbox Event Entity에 저장된 헤더 정보 포함
        messageDto.headers.forEach { (key, value) ->
            record.headers().add(RecordHeader(key, value.toByteArray()))
        }

        // 추가 메타데이터 헤더
        record.headers().add(RecordHeader("outbox-message-id", (messageDto.id?.toString() ?: "unknown").toByteArray()))
        record.headers().add(RecordHeader("aggregate-type", messageDto.aggregateType.toByteArray()))
        record.headers().add(RecordHeader("event-type", messageDto.eventType.toByteArray()))
        record.headers().add(RecordHeader("created-at", messageDto.createdAt.toString().toByteArray()))

        return record
    }
}
