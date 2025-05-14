plugins {
    id("java-library")
    kotlin("jvm") // Kotlin 플러그인 추가
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("jakarta.validation:jakarta.validation-api:3.1.1")
    implementation("com.fasterxml.uuid:java-uuid-generator:4.3.0")
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.3") // HttpStatus 등 springframework.web 사용

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
}
