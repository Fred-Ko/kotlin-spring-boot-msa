plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    // Spring Web에서 HttpStatus를 사용하기 위한 의존성
    implementation("org.springframework:spring-web")
    
    // Spring Boot Starter Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    
    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    // 순수 도메인 레이어는 외부 의존성이 없습니다.
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.assertj:assertj-core")
    testImplementation("io.mockk:mockk")
}
