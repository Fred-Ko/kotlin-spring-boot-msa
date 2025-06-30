package com.restaurant.payment.domain.repository

import com.restaurant.payment.domain.entity.PaymentMethod
import com.restaurant.payment.domain.vo.PaymentMethodId
import com.restaurant.payment.domain.vo.UserId

interface PaymentMethodRepository {
    fun save(paymentMethod: PaymentMethod): PaymentMethod

    fun findById(paymentMethodId: PaymentMethodId): PaymentMethod?

    fun findByUserId(userId: UserId): List<PaymentMethod>

    fun findByUserIdAndIsDefault(userId: UserId): PaymentMethod?
}
