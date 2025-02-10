package com.ddd.libs.outbox.model

import java.time.LocalDateTime
import java.util.UUID

data class OutboxEvent(
        val id: UUID,
        val aggregateType: String,
        val aggregateId: UUID,
        val eventType: String,
        val payload: String,
        val createdAt: LocalDateTime = LocalDateTime.now()
)
