package com.restaurant.config.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.util.*

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Ensure this filter runs very early
class CorrelationIdFilter : Filter {

    companion object {
        const val CORRELATION_ID_HEADER_NAME = "X-Correlation-Id"
        const val CORRELATION_ID_MDC_KEY = "correlationId" // Rule 37
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as? HttpServletRequest
        var correlationId = httpRequest?.getHeader(CORRELATION_ID_HEADER_NAME)

        if (correlationId.isNullOrBlank()) {
            correlationId = UUID.randomUUID().toString()
            // Optionally add the generated ID to the response header?
        }

        try {
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId) // Rule 37: Set MDC
            chain.doFilter(request, response)
        } finally {
            MDC.remove(CORRELATION_ID_MDC_KEY) // Rule 37: Remove MDC
        }
    }

    // init and destroy methods can be empty for this filter
}
