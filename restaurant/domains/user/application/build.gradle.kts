plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":domains:common"))
    implementation(project(":domains:user:domain"))
    implementation(project(":domains:user:infrastructure"))

    // Spring Boot 버전을 통일
    implementation("org.springframework.boot:spring-boot-starter:3.2.5")
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.5")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.5")

    implementation("org.mapstruct:mapstruct:1.6.3")

    // MapStruct 어노테이션 프로세서
    kapt("org.mapstruct:mapstruct-processor:1.6.3")

    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    // Kotest Spring 확장
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")

    // MockK
    testImplementation("io.mockk:mockk:1.13.8")

    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.4")
    testImplementation("org.assertj:assertj-core:3.27.3")

    // H2 데이터베이스
    testRuntimeOnly("com.h2database:h2:2.2.224")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
