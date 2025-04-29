package com.restaurant.user.application.port.input

import com.restaurant.user.application.dto.command.RegisterUserCommand // Command DTO 임포트 추가 (Package 변경됨)
import com.restaurant.user.domain.vo.UserId // UserId import

/**
 * 사용자 등록 유스케이스 인터페이스 (Rule App-Struct)
 * 사용자 ID(UserId)를 반환하도록 수정
 */
interface RegisterUserUseCase {
    fun register(command: RegisterUserCommand): UserId // 반환 타입 UserId로 명시
}
