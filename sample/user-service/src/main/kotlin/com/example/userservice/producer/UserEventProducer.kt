package com.example.userservice.producer

import com.example.events.UserEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.HashMap

@Service
class UserEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, HashMap<String, Any>>,
    @Value("\${kafka.topic.user-event}")
    private val userEventTopic: String
) {
    private val logger = LoggerFactory.getLogger(UserEventProducer::class.java)
    
    fun sendUserEvent(event: UserEvent) {
        try {
            logger.debug("Converting UserEvent to Map: $event")
            
            // Create a Map to represent the UserEvent
            val eventMap = HashMap<String, Any>()
            eventMap["email"] = event.email
            eventMap["eventType"] = event.eventType.name
            eventMap["id"] = event.id
            eventMap["timestamp"] = event.timestamp
            eventMap["userId"] = event.userId
            eventMap["userName"] = event.userName
            
            logger.debug("Sending event to Kafka topic '$userEventTopic' with key '${event.id}'")
            
            // Send to Kafka
            val future = kafkaTemplate.send(userEventTopic, event.id, eventMap)
            future.get() // Wait for send completion to catch any immediate errors
            
            logger.info("Successfully sent UserEvent: $event to topic: $userEventTopic")
        } catch (e: Exception) {
            logger.error("Failed to send UserEvent to Kafka: ${e.message}", e)
            throw RuntimeException("Failed to send event to Kafka: ${e.message}", e)
        }
    }
}
