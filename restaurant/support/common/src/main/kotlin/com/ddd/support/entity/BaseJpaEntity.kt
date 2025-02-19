package com.ddd.support.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime
import org.hibernate.proxy.HibernateProxy

@MappedSuperclass
abstract class BaseJpaEntity<ID : Serializable> : Serializable {

    abstract val id: ID?

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @Version @Column(nullable = false) var version: Long = 0

    fun getEntityIdOrThrow(): ID =
            id ?: throw IllegalStateException("${this::class.simpleName} ID is not initialized")

    fun getEntityIdOrNull(): ID? = id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false

        // 먼저 프록시를 언랩하여 실제 클래스를 구한다
        val thisEffectiveClass =
                (this as? HibernateProxy)?.hibernateLazyInitializer?.persistentClass
                        ?: this::class.java
        val otherEffectiveClass =
                (other as? HibernateProxy)?.hibernateLazyInitializer?.persistentClass
                        ?: other::class.java

        if (thisEffectiveClass != otherEffectiveClass) return false

        // 여기까지 왔다면 클래스 타입이 같다고 봄
        other as BaseJpaEntity<*>

        // 이제 ID 비교
        return when {
            this.id == null || other.id == null -> this === other
            else -> this.id == other.id
        }
    }
    override fun hashCode(): Int = id?.hashCode() ?: super.hashCode()

    override fun toString(): String = "${this::class.simpleName}(id=$id)"

    @PrePersist
    fun prePersist() {
        createdAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }
}
