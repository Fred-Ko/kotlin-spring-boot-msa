package com.restaurant.application.account.query.dto

/**
 * 커서 기반 페이징 결과 DTO
 *
 * @property items 조회된 아이템 목록
 * @property nextCursor 다음 페이지 요청 시 사용할 커서 (null이면 마지막 페이지)
 * @property hasNext 다음 페이지 존재 여부
 */
data class CursorPageDto<T>(
    val items: List<T>,
    val nextCursor: String?,
    val hasNext: Boolean,
)
