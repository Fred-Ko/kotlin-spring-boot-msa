package com.restaurant.user.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "addresses")
class AddressEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false, unique = true) // domainId는 unique해야 할 가능성이 높습니다.
    val domainId: UUID,
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    val streetAddress: String,
    @Column(nullable = true)
    val detailAddress: String?,
    @Column(nullable = false)
    val city: String,
    @Column(nullable = false)
    val state: String,
    @Column(nullable = false)
    val country: String,
    @Column(nullable = false)
    val zipCode: String,
    @Column(nullable = false)
    val isDefault: Boolean,
    @Column(nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
    @Column(nullable = false)
    val updatedAt: Instant = Instant.now(),
    @Column(nullable = false)
    val version: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity? = null // UserEntity와의 관계 추가
)
