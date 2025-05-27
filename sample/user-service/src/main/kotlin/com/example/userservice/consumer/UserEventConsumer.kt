package com.example.userservice.consumer

import com.example.events.UserEvent
import com.example.events.UserEventType
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class UserEventConsumer {
    private val logger = LoggerFactory.getLogger(UserEventConsumer::class.java)
    
    @KafkaListener(topics = ["\${kafka.topic.user-event}"], groupId = "\${spring.kafka.consumer.group-id}")
    fun consumeUserEvent(eventMap: Map<String, Any>) {
        try {
            logger.debug("Received event from Kafka: $eventMap")
            
            val event = UserEvent(
                id = eventMap["id"].toString(),
                eventType = UserEventType.valueOf(eventMap["eventType"].toString()),
                userId = eventMap["userId"].toString(),
                userName = eventMap["userName"].toString(),
                email = eventMap["email"].toString(),
                timestamp = (eventMap["timestamp"] as Number).toLong()
            )
            
            logger.info("Consumed UserEvent: $event")
            // TODO: Implement event processing logic
        } catch (e: Exception) {
            logger.error("Error processing Kafka message: ${e.message}", e)
        }
    }
}