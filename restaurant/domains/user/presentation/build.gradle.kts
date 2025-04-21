plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":domains:user:application"))
    implementation(project(":domains:user:domain"))
    implementation(project(":domains:common"))

    implementation("org.springframework.boot:spring-boot-starter:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.2")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")

    // HATEOAS 지원
    implementation("org.springframework.boot:spring-boot-starter-hateoas:3.3.2")

    // OpenAPI 3.0 & Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.2")
    testImplementation("io.mockk:mockk:1.13.17")
}
