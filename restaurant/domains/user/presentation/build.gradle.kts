plugins {
    kotlin("jvm")
}

dependencies {
    // Keep module-specific dependencies
    implementation(project(":domains:user:application"))
    implementation(project(":domains:common"))

    // Keep presentation-specific dependencies
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.5")
    implementation("org.springframework.boot:spring-boot-starter-hateoas:3.3.5")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.5")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

    // Common dependencies (kotlin, slf4j, jackson, test deps) are handled by subprojects block
    // implementation(libs.kotlin.stdlib)
    // implementation(libs.kotlin.logging.jvm)
    // implementation(libs.jackson.module.kotlin)
    // implementation(libs.jackson.datatype.jsr310)
    // implementation(libs.jakarta.validation.api)
    // implementation(libs.jakarta.servlet.api)

    // Test dependencies handled by subprojects block
}
