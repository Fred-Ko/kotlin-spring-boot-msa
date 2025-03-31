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
class AddressEntity(
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
  @Column(nullable = false) val street: String,
  @Column val detail: String,
  @Column(name = "zip_code", nullable = false) val zipCode: String,
  @Column(name = "is_default", nullable = false) val isDefault: Boolean,
) {
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  lateinit var user: UserEntity
  
  // 양방향 참조를 위한 생성자
  constructor(
    id: Long? = null,
    user: UserEntity,
    street: String,
    detail: String,
    zipCode: String,
    isDefault: Boolean
  ) : this(id, street, detail, zipCode, isDefault) {
    this.user = user
  }
  
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    
    other as AddressEntity
    
    if (id != null && other.id != null) {
      return id == other.id
    }
    
    if (street != other.street) return false
    if (detail != other.detail) return false
    if (zipCode != other.zipCode) return false
    
    return true
  }
  
  override fun hashCode(): Int {
    return id?.hashCode() ?: (street.hashCode() + zipCode.hashCode())
  }
  
  override fun toString(): String {
    return "AddressEntity(id=$id, street='$street', detail='$detail', zipCode='$zipCode', isDefault=$isDefault)"
  }
}
