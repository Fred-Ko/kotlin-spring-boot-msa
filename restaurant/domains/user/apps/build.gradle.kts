dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.mapstruct:mapstruct:1.6.3")

    runtimeOnly("com.h2database:h2")

    implementation(project(":domains:common"))
    implementation(project(":domains:user:domain"))
    implementation(project(":domains:user:application"))
    implementation(project(":domains:user:infrastructure"))
    implementation(project(":domains:user:presentation"))

    // OpenAPI 3.0 & Swagger UI
    implementation("org.webjars:webjars-locator-core:0.59")
    implementation("org.webjars:swagger-ui:5.20.1")

    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
