package com.restaurant.infrastructure.user.exception

class UserInfrastructureException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {
    companion object {
        fun entityNotFound(id: Long): UserInfrastructureException = UserInfrastructureException("ID가 ${id}인 사용자를 찾을 수 없습니다.")

        fun emailDuplicated(email: String): UserInfrastructureException = UserInfrastructureException("이메일 ${email}는 이미 사용 중입니다.")
    }
}
