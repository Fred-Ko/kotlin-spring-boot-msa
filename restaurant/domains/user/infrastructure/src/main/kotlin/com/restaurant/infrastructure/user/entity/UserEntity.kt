package com.restaurant.infrastructure.user.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class UserEntity(
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
  @Column(unique = true, nullable = false) val email: String,
  @Column(nullable = false) val password: String,
  @Column(nullable = false) val name: String,
  @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
  val addresses: MutableList<AddressEntity> = mutableListOf(),
  @Column(name = "created_at", nullable = false)
  val createdAt: LocalDateTime = LocalDateTime.now(),
  @Column(name = "updated_at", nullable = false)
  val updatedAt: LocalDateTime = LocalDateTime.now(),
)
