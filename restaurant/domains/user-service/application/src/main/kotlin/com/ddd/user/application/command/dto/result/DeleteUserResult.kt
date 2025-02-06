package com.ddd.user.application.command.dto.result

import java.util.UUID

data class DeleteUserResult(
        val id: UUID,
) {
    companion object {
        fun from(id: UUID): DeleteUserResult {
            return DeleteUserResult(id = id)
        }
    }
}
