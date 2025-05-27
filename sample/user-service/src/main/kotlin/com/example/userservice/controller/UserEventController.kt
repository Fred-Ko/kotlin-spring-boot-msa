package com.example.userservice.controller

import com.example.events.UserEvent
import com.example.events.UserEventType
import com.example.userservice.producer.UserEventProducer
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserEventController(private val userEventProducer: UserEventProducer) {
    private val logger = LoggerFactory.getLogger(UserEventController::class.java)
    
    @PostMapping("/events")
    fun createUserEvent(@RequestBody request: Map<String, Any>): ResponseEntity<String> {
        logger.info("Received user event request: $request")
        
        try {
            val event = UserEvent(
                id = UUID.randomUUID().toString(),
                eventType = UserEventType.USER_CREATED,  // Use the enum directly instead of parsing from the request
                userId = request["userId"].toString(),
                userName = request["userName"].toString(),
                email = request["email"].toString(),
                timestamp = System.currentTimeMillis()
            )
            
            logger.info("Created UserEvent: $event")
            userEventProducer.sendUserEvent(event)
            logger.info("Successfully sent user event with ID: ${event.id}")
            return ResponseEntity.ok("Event sent successfully with ID: ${event.id}")
        } catch (e: Exception) {
            logger.error("Error sending user event: ${e.message}", e)
            return ResponseEntity.internalServerError().body("Failed to send user event: ${e.message}")
        }
    }
}
