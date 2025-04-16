plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    // Spring Boot Starter Web (이미 spring-web을 포함)
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

    // Hateoas
    implementation("org.springframework.hateoas:spring-hateoas")
}
