plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":domains:common:domain"))
    implementation(project(":domains:common:application"))

    implementation("org.springframework.boot:spring-boot-starter-web:3.2.3")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.2.3")

    implementation("org.springframework.boot:spring-boot-starter-security:3.2.3") // Spring Security 추가
    // implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.3") // JPA는 presentation 레이어에서 직접 사용하지 않으므로 주석 처리
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5") // Kotlin Logging 추가

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.3")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.assertj:assertj-core:3.25.3")
}
