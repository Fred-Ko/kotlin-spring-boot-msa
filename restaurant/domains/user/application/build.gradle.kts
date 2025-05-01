plugins {
    kotlin("jvm")
    // Common plugins (jvm, spring, dependency-management, java-library) applied via subprojects
}

dependencies {
    api(project(":domains:user:domain"))

    implementation("org.springframework.boot:spring-boot-starter:3.3.5")
    implementation("org.springframework:spring-tx:6.1.6")
    implementation("org.springframework:spring-context:6.1.6")
    implementation("org.springframework.boot:spring-boot-starter-security:3.3.5")
    implementation("io.github.resilience4j:resilience4j-spring-boot3:2.2.0")
    implementation("org.springframework.boot:spring-boot-starter-aop:3.3.5")
    implementation("org.springframework.security:spring-security-crypto:6.3.1")
    testImplementation(project(":domains:user:domain")) { isTransitive = false }
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.5")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.24")
}

// Disable Java compile task as no Java code is expected
tasks.withType<JavaCompile> {
    enabled = false
}
