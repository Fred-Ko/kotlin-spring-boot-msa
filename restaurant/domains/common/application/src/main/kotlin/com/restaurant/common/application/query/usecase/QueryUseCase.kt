package com.restaurant.common.application.query.usecase

import com.restaurant.common.application.query.dto.Query

interface QueryUseCase<QUERY : Query, RESULT> {
    fun execute(query: QUERY): RESULT
}
