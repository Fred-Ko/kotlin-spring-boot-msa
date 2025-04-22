plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":domains:common"))

    // 순수 도메인 레이어는 외부 의존성이 없습니다.

    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")

    // MockK
    testImplementation("io.mockk:mockk:1.13.8")

    // 기존 테스트 의존성도 유지
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.4")
    testImplementation("org.assertj:assertj-core:3.27.3")

    // Rule 10 준수: HttpStatus 등 Spring Web 의존성 제거
    // implementation("org.springframework.boot:spring-boot-starter-web:3.3.2") 의존성 제거함
}
