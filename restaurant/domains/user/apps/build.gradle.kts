dependencies {
    implementation(project(":domains:user:presentation"))
    implementation(project(":domains:user:application"))
    implementation(project(":domains:user:infrastructure"))
    implementation(project(":domains:user:domain"))
    
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    // OpenAPI 3.0 & Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.4.0")
    
    // H2 데이터베이스
    runtimeOnly("com.h2database:h2")
    
    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
