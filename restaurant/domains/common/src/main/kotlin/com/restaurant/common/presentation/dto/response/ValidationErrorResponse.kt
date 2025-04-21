package com.restaurant.common.presentation.dto.response

import org.springframework.hateoas.RepresentationModel

data class ValidationErrorResponse(
    val type: String,
    val title: String,
    val detail: String,
    val errorCode: String,
    val timestamp: String,
    val correlationId: String?,
    val invalidParams: List<InvalidParam>,
) : RepresentationModel<ValidationErrorResponse>() {
    data class InvalidParam(
        val field: String,
        val reason: String,
    )
}
