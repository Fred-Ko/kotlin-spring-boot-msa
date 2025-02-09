package com.ddd.user.application.query

import com.ddd.user.application.dto.query.GetUserQueryResult

interface GetUserQuery {
    fun getUser(id: String): GetUserQueryResult
}
