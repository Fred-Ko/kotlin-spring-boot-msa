package com.restaurant.config

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.UUID

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Ensure this filter runs before others
class CorrelationIdFilter : Filter {
    companion object {
        const val CORRELATION_ID_HEADER_NAME = "X-Correlation-Id"
        const val CORRELATION_ID_MDC_KEY = "correlationId"
    }

    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain,
    ) {
        if (request is HttpServletRequest) {
            val correlationId = request.getHeader(CORRELATION_ID_HEADER_NAME) ?: UUID.randomUUID().toString()
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId)
            try {
                // Proceed with the request chain
                chain.doFilter(request, response)
            } finally {
                // Ensure MDC is cleared after the request is processed
                MDC.remove(CORRELATION_ID_MDC_KEY)
            }
        } else {
            // Not an HTTP request, just proceed
            chain.doFilter(request, response)
        }
    }

    // init and destroy methods can be empty for this filter
}
