package com.ddd.libs.outbox.model

import java.time.LocalDateTime

data class OutboxEvent(
        val id: String,
        val aggregateType: String,
        val aggregateId: String,
        val eventType: String,
        val payload: String,
        val createdAt: LocalDateTime = LocalDateTime.now()
)
