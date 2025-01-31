package com.ddd.libs.outbox.entity

import com.ddd.libs.outbox.model.OutboxEvent
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "outbox_events")
class OutboxEventJpaEntity(
        @Id @Column(length = 36) val id: String = "",
        @Column(nullable = false) val aggregateType: String = "",
        @Column(nullable = false) val aggregateId: String = "",
        @Column(nullable = false) val eventType: String = "",
        @Column(nullable = false, columnDefinition = "TEXT") val payload: String = "",
        @Column(nullable = false) val createdAt: LocalDateTime = LocalDateTime.now(),
        @Column(nullable = false) var published: Boolean = false,
) {
        fun toModel() =
                OutboxEvent(
                        id = id,
                        aggregateType = aggregateType,
                        aggregateId = aggregateId,
                        eventType = eventType,
                        payload = payload,
                        createdAt = createdAt
                )

        companion object {
                fun fromModel(event: OutboxEvent) =
                        OutboxEventJpaEntity(
                                id = event.id,
                                aggregateType = event.aggregateType,
                                aggregateId = event.aggregateId,
                                eventType = event.eventType,
                                payload = event.payload,
                                createdAt = event.createdAt
                        )
        }
}
