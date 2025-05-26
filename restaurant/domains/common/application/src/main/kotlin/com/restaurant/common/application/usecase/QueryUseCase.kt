package com.restaurant.common.application.usecase

import com.restaurant.common.application.dto.Query

interface QueryUseCase<QUERY : Query, RESULT> {
    fun execute(query: QUERY): RESULT
}
