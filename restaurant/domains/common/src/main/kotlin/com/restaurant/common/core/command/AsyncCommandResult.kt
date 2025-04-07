package com.restaurant.common.core.command

data class AsyncCommandResult(
    val success: Boolean,
    val correlationId: String,
    val jobId: String? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null,
    val errorDetails: Map<String, Any>? = null,
) {
    companion object {
        /**
         * 성공적인 AsyncCommandResult 생성
         */
        fun success(
            jobId: String,
            correlationId: String,
        ): AsyncCommandResult = AsyncCommandResult(success = true, jobId = jobId, correlationId = correlationId)

        /**
         * 실패한 AsyncCommandResult 생성
         */
        fun fail(
            correlationId: String,
            errorCode: String? = null,
            errorMessage: String? = null,
            errorDetails: Map<String, Any>? = null,
        ): AsyncCommandResult =
            AsyncCommandResult(
                success = false,
                correlationId = correlationId,
                errorCode = errorCode,
                errorMessage = errorMessage,
                errorDetails = errorDetails,
            )
    }
}
