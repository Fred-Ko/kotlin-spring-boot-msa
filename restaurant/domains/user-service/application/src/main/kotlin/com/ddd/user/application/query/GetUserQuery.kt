package com.ddd.user.application.query

import com.ddd.user.application.dto.result.GetUserQueryResult
import java.util.UUID

interface GetUserQuery {
    fun getUser(id: UUID): GetUserQueryResult
}
