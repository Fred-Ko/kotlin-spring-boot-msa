package com.restaurant.common.infrastructure.avro.envelope

/**
 * 임시 Envelope 클래스 (Avro 생성 전 컴파일 오류 방지용)
 * 빌드 과정에서 Avro 스키마로부터 자동 생성될 예정
 */
data class Envelope(
    val schemaVersion: String,
    val eventId: String,
    val timestamp: Long,
    val source: String,
    val aggregateType: String,
    val aggregateId: String,
    val eventType: String,
    val payload: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Envelope

        if (schemaVersion != other.schemaVersion) return false
        if (eventId != other.eventId) return false
        if (timestamp != other.timestamp) return false
        if (source != other.source) return false
        if (aggregateType != other.aggregateType) return false
        if (aggregateId != other.aggregateId) return false
        if (eventType != other.eventType) return false
        if (!payload.contentEquals(other.payload)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = schemaVersion.hashCode()
        result = 31 * result + eventId.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + source.hashCode()
        result = 31 * result + aggregateType.hashCode()
        result = 31 * result + aggregateId.hashCode()
        result = 31 * result + eventType.hashCode()
        result = 31 * result + payload.contentHashCode()
        return result
    }
}
