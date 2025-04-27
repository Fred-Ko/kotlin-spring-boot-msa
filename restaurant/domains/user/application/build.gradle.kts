plugins {
    // kotlin("jvm") // Provided by subprojects block
    `java-library`
    // Common plugins (jvm, spring, dependency-management, java-library) applied via subprojects
}

dependencies {
    // Keep specific API dependencies
    api(project(":domains:user:domain"))
    api(project(":domains:common"))
    // Keep implementation dependencies
    // implementation(project(":domains:user:infrastructure")) // Remove unified infra dependency
    implementation(project(":domains:user:infrastructure:persistence")) // Add persistence dependency for Query Handlers

    // Keep application-specific dependencies
    implementation(libs.spring.boot.starter) // Use alias
    implementation(libs.spring.tx) // Use alias
    implementation(libs.spring.context) // Use alias
    implementation(libs.spring.boot.starter.security) // Use alias
    implementation(libs.resilience4j.spring.boot3) // Use alias
    implementation(libs.spring.boot.starter.aop) // Use alias

    // Spring Security Crypto for PasswordEncoder
    implementation(libs.spring.security.crypto) // Use alias

    // Common dependencies (kotlin, slf4j, jackson, test deps) are handled by subprojects block

    // Test dependencies specific to Application layer
    testImplementation(project(":domains:user:domain")) { isTransitive = false } // For testing with domain classes
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5") // Use string notation
}

// Disable Java compile task as no Java code is expected
tasks.withType<JavaCompile> {
    enabled = false
}
