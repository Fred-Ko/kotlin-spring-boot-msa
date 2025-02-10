package com.ddd.user.presentation.api.v1.query.dto.response

import com.ddd.user.application.dto.result.GetUserQueryResult
import com.ddd.user.application.dto.result.ListUsersQueryResult
import java.util.UUID

data class GetUserResponse(
        val id: UUID,
        val email: String,
        val name: String,
        val phoneNumber: String,
        val street: String,
        val city: String,
        val state: String,
        val zipCode: String,
        val active: Boolean,
) {
    companion object {
        fun fromQueryResult(queryResult: GetUserQueryResult): GetUserResponse {
            return GetUserResponse(
                    id = queryResult.id,
                    email = queryResult.email,
                    name = queryResult.name,
                    phoneNumber = queryResult.phoneNumber,
                    street = queryResult.street,
                    city = queryResult.city,
                    state = queryResult.state,
                    zipCode = queryResult.zipCode,
                    active = queryResult.active,
            )
        }
    }
}

data class ListUsersResponse(
        val users: List<User>,
        val totalCount: Long,
        val totalPages: Int,
) {
    data class User(val id: UUID, val email: String, val name: String, val active: Boolean) {
        companion object {
            fun fromQueryResultUser(queryResultUser: ListUsersQueryResult.User): User {
                return User(
                        id = queryResultUser.id,
                        email = queryResultUser.email,
                        name = queryResultUser.name,
                        active = queryResultUser.active,
                )
            }
        }
    }

    companion object {
        fun fromQueryResult(queryResult: ListUsersQueryResult): ListUsersResponse {
            return ListUsersResponse(
                    users = queryResult.users.map { User.fromQueryResultUser(it) },
                    totalCount = queryResult.totalCount,
                    totalPages = queryResult.totalPages,
            )
        }
    }
}
