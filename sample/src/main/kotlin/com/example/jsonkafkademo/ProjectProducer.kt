package com.example.jsonkafkademo

import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class ProjectProducer(
    private val kafkaTemplate: KafkaTemplate<String, Project>,
    @Value("\${spring.kafka.template.default-topic:projects-topic}")
    private val topic: String
) {
    fun sendProject(project: Project) {
        kafkaTemplate.send(topic, project)
    }
}
