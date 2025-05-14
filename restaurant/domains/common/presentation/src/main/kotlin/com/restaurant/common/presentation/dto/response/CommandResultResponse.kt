package com.restaurant.common.presentation.dto.response

data class CommandResultResponse(
    val status: String = "SUCCESS",
    val message: String? = null
)
