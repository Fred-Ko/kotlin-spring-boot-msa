package com.restaurant.presentation.account.v1.query.dto.response

/**
 * 커서 기반 페이징 응답 DTO
 *
 * @property items 조회된 아이템 목록
 * @property nextCursor 다음 페이지 요청 시 사용할 커서 (null이면 마지막 페이지)
 * @property hasNext 다음 페이지 존재 여부
 */
data class CursorPageResponseV1<T>(
    val items: List<T>,
    val nextCursor: String?,
    val hasNext: Boolean,
)
