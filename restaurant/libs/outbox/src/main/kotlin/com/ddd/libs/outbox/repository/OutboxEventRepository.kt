package com.ddd.libs.outbox.repository

import com.ddd.libs.outbox.entity.OutboxEventJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface OutboxEventRepository : JpaRepository<OutboxEventJpaEntity, String> {
    @Query("SELECT e FROM OutboxEventJpaEntity e WHERE e.published = false ORDER BY e.createdAt")
    fun findUnpublishedEvents(): List<OutboxEventJpaEntity>
}