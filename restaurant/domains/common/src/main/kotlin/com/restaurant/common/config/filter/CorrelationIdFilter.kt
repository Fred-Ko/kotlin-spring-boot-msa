package com.restaurant.common.config.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * A servlet filter that extracts or generates correlation IDs for requests.
 * The correlation ID is used for request tracing across services.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class CorrelationIdFilter : Filter {
    companion object {
        const val CORRELATION_ID_HEADER = "X-Correlation-Id"
        const val CORRELATION_ID_MDC_KEY = "correlationId"
    }

    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain,
    ) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        try {
            // Get correlationId from header or generate a new one
            val correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER) ?: UUID.randomUUID().toString()

            // Set in MDC for logging
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId)

            // Add to response
            httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId)

            // Continue filter chain
            chain.doFilter(request, response)
        } finally {
            // Clear MDC
            MDC.remove(CORRELATION_ID_MDC_KEY)
        }
    }
}
