package com.restaurant.presentation.user.v1.command.dto.request

// 주소 삭제 시 별도 요청 본문이 필요 없을 수 있으나, ktlint 규칙상 파일 생성
data class DeleteAddressRequestV1(
    // 필요한 필드가 있다면 추가
    val dummy: String? = null, // ktlint 회피용 임시 필드
)
