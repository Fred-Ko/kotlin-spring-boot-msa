package com.restaurant.infrastructure.user.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "user_addresses")
data class AddressEntity(
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  val user: UserEntity,
  @Column(nullable = false) val street: String,
  @Column val detail: String,
  @Column(name = "zip_code", nullable = false) val zipCode: String,
  @Column(name = "is_default", nullable = false) val isDefault: Boolean,
)
