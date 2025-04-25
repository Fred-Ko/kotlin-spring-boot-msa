package com.restaurant.domain.account.vo

data class TransactionId private constructor(
    val value: Long,
) {
    init {
        require(value > 0) { "TransactionId는 0보다 커야 합니다." }
    }

    companion object {
        fun of(value: Long): TransactionId = TransactionId(value)

        /**
         * 문자열 값으로부터 TransactionId를 생성합니다.
         *
         * @param value 문자열 값 (숫자로 변환 가능해야 함)
         * @return TransactionId 객체
         */
        fun of(value: String): TransactionId = of(value.toLong())
    }

    override fun toString(): String = value.toString()
}
