package com.restaurant.common.core.command

data class CommandResult(
    val success: Boolean,
    val correlationId: String,
    val errorCode: String? = null,
    val errorMessage: String? = null,
    val errorDetails: Map<String, Any>? = null,
) {
    companion object {
        /**
         * 성공적인 CommandResult 생성
         */
        fun success(correlationId: String): CommandResult = CommandResult(success = true, correlationId = correlationId)

        /**
         * 실패한 CommandResult 생성
         */
        fun fail(
            correlationId: String,
            errorCode: String? = null,
            errorMessage: String? = null,
            errorDetails: Map<String, Any>? = null,
        ): CommandResult =
            CommandResult(
                success = false,
                correlationId = correlationId,
                errorCode = errorCode,
                errorMessage = errorMessage,
                errorDetails = errorDetails,
            )
    }
}
