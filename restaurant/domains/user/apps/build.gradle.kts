plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":domains:user:presentation"))
    implementation(project(":domains:user:infrastructure"))
    implementation(project(":domains:common"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // HATEOAS 지원
    implementation("org.springframework.boot:spring-boot-starter-hateoas")

    // OpenAPI 3.0 & Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4")
    implementation("org.webjars:webjars-locator-core:0.59")

    // Database
    runtimeOnly("com.h2database:h2:2.3.232")

    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.17")
}
