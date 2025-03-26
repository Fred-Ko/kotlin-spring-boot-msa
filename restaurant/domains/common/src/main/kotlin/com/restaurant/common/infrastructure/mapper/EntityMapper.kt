package com.restaurant.common.infrastructure.mapper

interface EntityMapper<D, E> {
  fun toEntity(domain: D): E

  fun toDomain(entity: E): D
}
