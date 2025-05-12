package com.restaurant.outbox.infrastructure.kafka

import com.restaurant.outbox.application.port.model.OutboxMessage 
import com.restaurant.outbox.infrastructure.exception.OutboxException
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component





@Component
class OutboxMessageSender(
    private val kafkaTemplate: KafkaTemplate<String, ByteArray>,
    
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    

    
    fun processAndSendMessage(messageDto: OutboxMessage) { 
        try {
            
            

            val kafkaMessage = createKafkaMessage(messageDto)
            val sendResult = kafkaTemplate.send(kafkaMessage).get() 

            logger.info(
                "Successfully sent message to Kafka. Topic: {}, Partition: {}, Offset: {}, MessageId: {}",
                messageDto.topic,
                sendResult.recordMetadata.partition(),
                sendResult.recordMetadata.offset(),
                messageDto.id,
            )
            

        } catch (e: Exception) {
            
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
            .setHeader(KafkaHeaders.KEY, messageDto.aggregateId) 
            .also { builder ->
                messageDto.headers.forEach { (key, value) ->
                    builder.setHeader(key, value)
                }
            }.build()

    
    
}
