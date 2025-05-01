package com.restaurant.common.presentation.dto.response

import org.springframework.hateoas.RepresentationModel
import java.util.UUID

/**
 * Standard response for command results, including status, message, and correlation ID.
 */
data class CommandResultResponse(
    val status: String = "SUCCESS",
    val message: String,
    val correlationId: String = UUID.randomUUID().toString(),
) : RepresentationModel<CommandResultResponse>()
