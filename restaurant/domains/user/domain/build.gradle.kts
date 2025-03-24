plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":domains:common"))

    // 순수 도메인 레이어는 외부 의존성이 없습니다.
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.assertj:assertj-core")
    testImplementation("io.mockk:mockk")
}
