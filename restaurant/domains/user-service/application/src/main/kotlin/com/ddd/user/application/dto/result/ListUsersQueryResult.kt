package com.ddd.user.application.dto.result

import java.util.UUID

data class ListUsersQueryResult(
        val users: List<User>,
        val totalCount: Long,
        val totalPages: Int,
) {
    data class User(val id: UUID, val email: String, val name: String, val active: Boolean)
}
