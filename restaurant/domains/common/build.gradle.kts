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

}
