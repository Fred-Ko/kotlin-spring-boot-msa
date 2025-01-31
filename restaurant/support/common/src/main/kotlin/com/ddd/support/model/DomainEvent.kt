import java.time.Instant
import java.util.UUID

interface DomainEvent {
    val eventId: UUID
    val eventType: String
    val aggregateId: UUID
    val timestamp: Instant
    val version: Long
    val metadata: Map<String, String>

    /** 이벤트의 비즈니스 데이터를 포함하는 페이로드 구체적인 이벤트 클래스에서 구현 */
    val payload: Any
}
