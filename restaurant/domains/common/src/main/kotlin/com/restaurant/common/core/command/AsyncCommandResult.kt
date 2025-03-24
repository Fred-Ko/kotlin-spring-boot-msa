package com.restaurant.common.core.command

data class AsyncCommandResult(
        val success: Boolean,
        val jobId: String? = null,
        val errorCode: String? = null
)
