plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":domains:user:application"))
    implementation(project(":domains:common"))
    
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    implementation("org.mapstruct:mapstruct:1.6.3")

    // MapStruct 어노테이션 프로세서 - Kotlin에서는 kapt 사용
    kapt("org.mapstruct:mapstruct-processor:1.6.3")
    
    // HATEOAS 지원
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    
    // OpenAPI 3.0 & Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.4.0")
    
    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
