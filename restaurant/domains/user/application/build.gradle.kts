plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":domains:common"))
    implementation(project(":domains:user:domain"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-tx")
    implementation("org.springframework:spring-web")
    implementation("org.mapstruct:mapstruct:1.6.3")

    // MapStruct 어노테이션 프로세서
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.assertj:assertj-core")
    testImplementation("io.mockk:mockk")
}
