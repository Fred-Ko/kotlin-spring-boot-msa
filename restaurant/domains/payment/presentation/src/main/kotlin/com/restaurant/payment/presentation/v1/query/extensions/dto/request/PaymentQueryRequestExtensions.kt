package com.restaurant.payment.presentation.v1.query.extensions.dto.request

import com.restaurant.payment.application.query.dto.GetPaymentByIdQuery
import com.restaurant.payment.presentation.v1.query.dto.request.GetPaymentByIdRequestV1


/**
 * Payment Query Request DTO Extensions (Rule 5, 7, 58, 59)
 * Presentation Request -> Application Query DTO conversion
 */

/**
 * GetPaymentByIdRequestV1 -> GetPaymentByIdQuery 변환
 */
fun GetPaymentByIdRequestV1.toQuery(): GetPaymentByIdQuery {
    return GetPaymentByIdQuery(
        paymentId = this.paymentId,
    )
} 