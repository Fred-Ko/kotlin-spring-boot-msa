plugins {
    kotlin("jvm")
}

dependencies {
    api("jakarta.validation:jakarta.validation-api:3.0.2") // VO 유효성 검사 어노테이션용
    // 다른 common 하위 모듈 의존성 없음 (일반적으로)
}
