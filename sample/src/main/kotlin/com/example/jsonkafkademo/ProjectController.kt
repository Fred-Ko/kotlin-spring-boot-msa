package com.example.jsonkafkademo

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/projects")
class ProjectController(
    private val projectProducer: ProjectProducer
) {
    @PostMapping
    fun createProject(@RequestBody project: Project): ResponseEntity<String> {
        projectProducer.sendProject(project)
        return ResponseEntity.ok("Project sent to Kafka.")
    }
}
