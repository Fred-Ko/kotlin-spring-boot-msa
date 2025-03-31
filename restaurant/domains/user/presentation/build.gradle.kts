plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":domains:user:application"))
    implementation(project(":domains:user:domain"))
    implementation(project(":domains:common"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // HATEOAS 지원
    implementation("org.springframework.boot:spring-boot-starter-hateoas")

    // OpenAPI 3.0 & Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4")

    // MapStruct
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    kapt("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.17")
}
