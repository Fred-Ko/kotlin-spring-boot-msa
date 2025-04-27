plugins {
    // kotlin("jvm") // Provided by subprojects block
    alias(libs.plugins.kotlin.spring) // Apply spring plugin
}

dependencies {
    // Keep module-specific dependencies
    implementation(project(":domains:user:application"))
    implementation(project(":config"))

    // Keep presentation-specific dependencies
    implementation(libs.spring.boot.starter.web) // Use alias
    implementation(libs.spring.boot.starter.hateoas) // Use alias
    implementation(libs.spring.boot.starter.validation) // Use alias
    implementation(libs.springdoc.openapi.starter.webmvc.ui) // Use alias

    // Common dependencies (kotlin, slf4j, jackson, test deps) are handled by subprojects block
    // implementation(libs.kotlin.stdlib)
    // implementation(libs.kotlin.logging.jvm)
    // implementation(libs.jackson.module.kotlin)
    // implementation(libs.jackson.datatype.jsr310)
    // implementation(libs.jakarta.validation.api)
    // implementation(libs.jakarta.servlet.api)

    // Test dependencies handled by subprojects block
}
