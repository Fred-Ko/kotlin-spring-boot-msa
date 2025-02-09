package com.ddd.user.application.dto.result

data class ListUsersQueryResult(
        val users: List<User>,
        val totalCount: Long,
        val totalPages: Int,
) {
    data class User(val id: String, val email: String, val name: String, val active: Boolean)
}
