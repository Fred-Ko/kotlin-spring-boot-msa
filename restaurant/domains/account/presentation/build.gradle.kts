plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":domains:account:application"))
    implementation(project(":domains:account:domain"))
    implementation(project(":domains:common"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // HATEOAS 지원
    implementation("org.springframework.boot:spring-boot-starter-hateoas")

    // OpenAPI 3.0 & Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4")

    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.17")
}
