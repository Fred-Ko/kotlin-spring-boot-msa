plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    // 필수 Spring Framework 의존성만 유지
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-hateoas:3.3.2")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
}
