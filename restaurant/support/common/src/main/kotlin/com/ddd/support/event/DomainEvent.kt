package com.ddd.support.event

abstract class DomainEvent<E, A> {
    // 이벤트의 고유 식별자
    abstract val eventId: E

    // 애그리게이트 루트의 식별자
    abstract val aggregateId: A

    // 이벤트의 종류
    abstract val eventType: String

    // 이벤트와 관련된 데이터
    abstract val eventData: Map<String, Any>

    // 이벤트의 타임스탬프
    abstract val timestamp: Long

    // 이벤트의 버전
    abstract val version: Int

    // 이벤트 데이터에서 특정 키의 값을 가져오는 함수
    fun getDataValue(key: String): Any? = eventData[key]

    // 이벤트 데이터에서 특정 키의 값을 타입 캐스팅하여 가져오는 함수
    @Suppress("UNCHECKED_CAST") fun <T> getDataValueAs(key: String): T? = eventData[key] as? T

    // 이벤트가 특정 시간 이후에 발생했는지 확인하는 함수
    fun isAfter(otherTimestamp: Long): Boolean = timestamp > otherTimestamp

    // 이벤트가 특정 시간 이전에 발생했는지 확인하는 함수
    fun isBefore(otherTimestamp: Long): Boolean = timestamp < otherTimestamp

    // 이벤트의 문자열 표현을 반환하는 함수
    override fun toString(): String =
            "DomainEvent(eventId=$eventId, aggregateId=$aggregateId, eventType=$eventType, timestamp=$timestamp, version=$version)"

    // 이벤트의 해시코드를 계산하는 함수
    override fun hashCode(): Int =
            eventId.hashCode() * 31 +
                    aggregateId.hashCode() * 17 +
                    eventType.hashCode() * 13 +
                    timestamp.hashCode() * 7 +
                    version

    // 이벤트의 동등성을 비교하는 함수
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DomainEvent<*, *>) return false

        return eventId == other.eventId &&
                aggregateId == other.aggregateId &&
                eventType == other.eventType &&
                timestamp == other.timestamp &&
                version == other.version
    }
}
