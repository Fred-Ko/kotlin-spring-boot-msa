plugins {
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":domains:common"))
    implementation(project(":domains:account:domain"))
    implementation(project(":domains:account:infrastructure"))

    // Spring Boot 버전을 통일
    implementation("org.springframework.boot:spring-boot-starter:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-hateoas:3.3.2")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")

    implementation("org.mapstruct:mapstruct:1.6.3")
    implementation("org.springframework:spring-web:6.1.4")

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
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.4")
    testImplementation("org.assertj:assertj-core:3.27.3")

    // H2 데이터베이스
    testRuntimeOnly("com.h2database:h2:2.3.232")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
