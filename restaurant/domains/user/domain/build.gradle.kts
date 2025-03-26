plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":domains:common"))

    // 순수 도메인 레이어는 외부 의존성이 없습니다.
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.4")
    testImplementation("org.assertj:assertj-core:3.27.3")

}
