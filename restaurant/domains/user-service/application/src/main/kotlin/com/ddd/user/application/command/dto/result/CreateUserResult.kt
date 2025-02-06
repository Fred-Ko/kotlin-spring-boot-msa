package com.ddd.user.application.command.dto.result

import com.ddd.user.domain.model.aggregate.User

data class CreateUserResult(
        val id: String,
        val name: String,
        val email: String,
) {
    companion object {
        fun from(user: User): CreateUserResult {
            return CreateUserResult(
                    id = user.id.toString(),
                    name = user.name.value,
                    email = user.email.value,
            )
        }
    }
}
