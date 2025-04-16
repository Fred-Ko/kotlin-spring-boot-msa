package com.restaurant.presentation.account.v1.extensions.response

import com.restaurant.application.account.query.dto.TransactionDto
import com.restaurant.common.core.query.dto.CursorPageDto
import com.restaurant.presentation.account.v1.query.dto.response.CursorPageResponseV1
import com.restaurant.presentation.account.v1.query.dto.response.TransactionResponseV1

// CursorPageDto<TransactionDto> 확장 함수
fun CursorPageDto<TransactionDto>.toResponse(): CursorPageResponseV1<TransactionResponseV1> =
    CursorPageResponseV1(
        items = this.items.map { it.toResponse() },
        nextCursor = this.nextCursor,
        hasNext = this.hasNext,
    )

// TransactionDto 확장 함수
fun TransactionDto.toResponse(): TransactionResponseV1 =
    TransactionResponseV1(
        id = this.id,
        accountId = this.accountId,
        type = this.type,
        amount = this.amount,
        orderId = this.orderId,
        timestamp = this.timestamp,
        dateTime = this.dateTime,
    )
