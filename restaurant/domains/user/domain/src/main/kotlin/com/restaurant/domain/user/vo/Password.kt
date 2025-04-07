package com.restaurant.domain.user.vo

import java.security.MessageDigest
import kotlin.ConsistentCopyVisibility

@ConsistentCopyVisibility
data class Password private constructor(
    val encodedValue: String,
) {
    init {
        require(encodedValue.isNotBlank()) { "비밀번호는 빈 값일 수 없습니다." }
    }

    companion object {
        private const val SALT = "ddd-user-salt"
        private const val MIN_LENGTH = 8

        fun of(rawPassword: String): Password {
            require(rawPassword.length >= MIN_LENGTH) { "비밀번호는 최소 ${MIN_LENGTH}글자 이상이어야 합니다." }
            return Password(encode(rawPassword))
        }

        fun fromEncoded(encodedPassword: String): Password = Password(encodedPassword)

        private fun encode(rawPassword: String): String {
            val saltedPassword = rawPassword + SALT
            val messageDigest = MessageDigest.getInstance("SHA-256")
            val digest = messageDigest.digest(saltedPassword.toByteArray())
            return digest.fold("") { str, byte -> str + "%02x".format(byte) }
        }
    }

    fun matches(rawPassword: String): Boolean = encodedValue == encode(rawPassword)

    private fun encode(rawPassword: String): String = Companion.encode(rawPassword)

    override fun toString(): String = "********" // 보안상 실제 값 대신 마스킹 처리
}
