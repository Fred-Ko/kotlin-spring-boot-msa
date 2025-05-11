package com.restaurant.outbox.infrastructure.kafka

import com.restaurant.outbox.application.port.model.OutboxMessage // OutboxEventEntity 대신 사용
import com.restaurant.outbox.infrastructure.exception.OutboxException
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
// import org.springframework.transaction.annotation.Transactional // 상태 변경 로직 제거로 불필요
// import java.time.Instant // 상태 변경 로직 제거로 불필요
// import com.restaurant.outbox.application.port.model.OutboxMessageStatus // 상태 변경 로직 제거
// import com.restaurant.outbox.infrastructure.persistence.repository.JpaOutboxEventRepository // 의존성 제거

@Component
class OutboxMessageSender(
    private val kafkaTemplate: KafkaTemplate<String, ByteArray>,
    // private val outboxEventRepository: JpaOutboxEventRepository, // JpaOutboxEventRepository 의존성 제거
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    // private val maxRetries = 3 // OutboxPoller로 이동

    // @Transactional // 이 클래스는 더 이상 DB 트랜잭션을 직접 다루지 않음
    fun processAndSendMessage(messageDto: OutboxMessage) { // 파라미터를 OutboxMessage로 변경
        try {
            // Retry 및 MaxRetriesExceeded 처리는 OutboxPoller에서 수행
            // Sender는 순수하게 메시지 전송 시도 및 결과/예외 반환에 집중

            val kafkaMessage = createKafkaMessage(messageDto)
            val sendResult = kafkaTemplate.send(kafkaMessage).get() // 동기 전송으로 결과 확인

            logger.info(
                "Successfully sent message to Kafka. Topic: {}, Partition: {}, Offset: {}, MessageId: {}",
                messageDto.topic,
                sendResult.recordMetadata.partition(),
                sendResult.recordMetadata.offset(),
                messageDto.id,
            )
            // 성공 시 별도 상태 업데이트 없음. Poller가 처리.

        } catch (e: Exception) {
            // 전송 실패 시 OutboxException.KafkaSendFailedException 발생시켜 Poller가 처리하도록 함
            logger.error(
                "Failed to send message to Kafka. MessageId: {}, Error: {}",
                messageDto.id,
                e.message,
                e,
            )
            throw OutboxException.KafkaSendFailedException(
                message = "Failed to send message to Kafka for OutboxMessage ID ${messageDto.id}: ${e.message}",
                cause = e,
            )
        }
    }

    private fun createKafkaMessage(messageDto: OutboxMessage): Message<ByteArray> =
        MessageBuilder
            .withPayload(messageDto.payload)
            .setHeader(KafkaHeaders.TOPIC, messageDto.topic)
            .setHeader(KafkaHeaders.KEY, messageDto.aggregateId) // Rule 83: aggregateId는 String
            .also { builder ->
                messageDto.headers.forEach { (key, value) ->
                    builder.setHeader(key, value)
                }
            }.build()

    // handleMaxRetriesExceeded 및 handleSendFailure 메서드는 OutboxPoller로 책임 이동
    // 또는 OutboxPoller에서 해당 예외를 catch하여 OutboxMessageRepository를 통해 상태 업데이트
}
