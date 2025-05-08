package com.restaurant.user.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "addresses")
class AddressEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    val domainId: UUID,
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    val streetAddress: String,
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
    @Column(nullable = false)
    val createdAt: Instant,
    @Column(nullable = false)
    val updatedAt: Instant,
    @Column(nullable = false)
    val version: Long,
)
