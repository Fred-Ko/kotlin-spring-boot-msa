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
import java.util.*

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Ensure this filter runs early
class MdcFilter : Filter {
    companion object {
        const val CORRELATION_ID_HEADER_NAME = "X-Correlation-Id"
        const val CORRELATION_ID_MDC_KEY = "correlationId"
    }

    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain,
    ) {
        val httpRequest = request as? HttpServletRequest
        // Try to get correlationId from header, generate if not present
        val correlationId =
            httpRequest?.getHeader(CORRELATION_ID_HEADER_NAME)?.takeIf { it.isNotBlank() }
                ?: UUID.randomUUID().toString()

        MDC.put(CORRELATION_ID_MDC_KEY, correlationId)
        try {
            chain.doFilter(request, response)
        } finally {
            MDC.remove(CORRELATION_ID_MDC_KEY)
        }
    }
    // init and destroy methods can be omitted if not needed
} 
