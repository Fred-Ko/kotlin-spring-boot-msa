package com.ddd.user.application.command.dto.result

import com.ddd.user.domain.model.aggregate.User

data class UpdateUserResult(
        val id: String,
) {
    companion object {
        fun from(user: User): UpdateUserResult {
            return UpdateUserResult(id = user.id.toString())
        }
    }
}
