plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":domains:common:domain")) // ErrorCode, DomainException 등 참조
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.5") // ControllerAdvice, Filter 등
    implementation("org.springframework.boot:spring-boot-starter-hateoas:3.2.5") // CommandResultResponse
    implementation("org.springframework.boot:spring-boot-starter-security:3.2.5") // SecurityConfig
    implementation("org.springframework.security:spring-security-crypto:6.2.4") // PasswordEncoder (SecurityConfig)
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation(project(":independent:outbox")) // OutboxException 처리 위해 추가
}
