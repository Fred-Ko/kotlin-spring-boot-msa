package com.ddd.support.aggregate

import com.ddd.support.event.DomainEvent
import java.util.Collections

abstract class AbstractAggregateRoot<T : AbstractAggregateRoot<T>> {

    private val domainEvents: MutableList<DomainEvent<*, *>> = ArrayList()

    /** 도메인 이벤트 등록 */
    @Suppress("UNCHECKED_CAST")
    protected fun registerEvent(event: DomainEvent<*, *>): T {
        domainEvents.add(event)
        return this as T
    }

    /** 등록된 모든 도메인 이벤트 반환 */
    fun domainEvents(): Collection<DomainEvent<*, *>> {
        return Collections.unmodifiableList(domainEvents)
    }

    /** 도메인 이벤트 초기화 */
    fun clearEvents() {
        domainEvents.clear()
    }
}
