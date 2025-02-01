package com.ddd.restaurant.domain.model.entity

import java.util.UUID

data class Menu(
    val id: UUID,
    val name: String,
    val price: Double
) 