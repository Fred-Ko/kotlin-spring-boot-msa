package com.ddd.user.application.query

import com.ddd.user.application.dto.result.ListUsersQueryResult

interface ListUsersQuery {
    fun listUsers(page: Int, size: Int): ListUsersQueryResult
}
