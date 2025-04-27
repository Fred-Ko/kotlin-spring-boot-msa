plugins {
    // kotlin("jvm") // Provided by subprojects block
    alias(libs.plugins.spring.boot) // Apply boot plugin
    // id("io.spring.dependency-management") // Provided by subprojects
    alias(libs.plugins.kotlin.spring) // Apply spring plugin
}

dependencies {
    implementation(project(":domains:common")) // For common exceptions, ErrorCode interface

    // Spring Boot Starter Web (for ExceptionHandler, Filters, etc.)
    // implementation(libs.spring.boot.starter.web) // Use alias
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.1")

    // Validation (for MethodArgumentNotValidException handling)
    // implementation(libs.spring.boot.starter.validation) // Use alias
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.1")

    // Springdoc OpenAPI (for Swagger Config)
    // implementation(libs.springdoc.openapi.starter.webmvc.ui) // Use alias
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0") // Keep existing version

    // Logging
    // implementation(libs.kotlin.logging.jvm) // Provided by subprojects
    // implementation(libs.logback.classic) // Provided by subprojects
    implementation(libs.logstash.logback.encoder) // Keep specific encoder

    // Test dependencies
    // testImplementation(libs.spring.boot.starter.test) // Provided by subprojects

    // Added HATEOAS for CommandResultResponse
    // implementation(libs.spring.boot.starter.hateoas) // Use alias
    implementation("org.springframework.boot:spring-boot-starter-hateoas:3.3.1")
}

// Disable BootJar task if this module is not meant to be executable
tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    enabled = false
}
tasks.withType<Jar> {
    enabled = true // Ensure standard Jar is created
}
