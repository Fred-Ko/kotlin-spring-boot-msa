package com.restaurant.common.core.command

data class CommandResult(
    val success: Boolean,
    val correlationId: String? = null,
    val errorCode: String? = null,
)
