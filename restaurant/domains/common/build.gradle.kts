plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    // Spring Boot Starter Web (이미 spring-web을 포함)
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")
}
