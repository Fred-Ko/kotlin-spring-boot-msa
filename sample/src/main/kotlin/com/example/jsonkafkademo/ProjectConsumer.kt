package com.example.jsonkafkademo

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
class ProjectConsumer {
    @KafkaListener(
        topics = ["projects-topic"],
        groupId = "project-consumer-group"
    )
    fun listen(@Payload project: Project) {
        println("[Consumer] Received project: $project")
    }
}
