plugins {
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.jetbrains.kotlin.jvm") version "2.1.20"
    id("org.jetbrains.kotlin.plugin.spring") version "2.1.20"
}

dependencies {
    // Core Application Dependencies
    implementation(project(":config"))
    implementation(project(":domains:user:presentation"))
    implementation(project(":domains:user:application"))
    implementation(project(":domains:user:infrastructure:persistence"))
    implementation(project(":domains:user:infrastructure:messaging"))

    // Common module dependencies (if needed directly by the app runner)
    implementation(project(":domains:common"))
    implementation(project(":domains:common:infrastructure"))

    // Spring Boot Starter Web
    implementation(libs.spring.boot.starter.web) {
        // Exclude default Tomcat if using Undertow or other server
        // exclude group: 'org.springframework.boot', module: 'spring-boot-starter-tomcat'
    }

    // Include Undertow if Tomcat is excluded
    // implementation libs.spring.boot.starter.undertow

    // Actuator for monitoring
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Springdoc OpenAPI for Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

    // Other potential dependencies
    // implementation(libs.spring.cloud.starter.config) // If using Spring Cloud Config
    // implementation(libs.micrometer.registry.prometheus) // Example Micrometer registry

    // H2 Console (개발/테스트용)
    runtimeOnly("com.h2database:h2")
}

// Spring Boot specific configurations
springBoot {
    mainClass.set("com.restaurant.user.app.UserApplication")
}

// Ensure the bootJar task is configured
tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    enabled = true
    // classifier = "boot"
}

// Optionally disable the plain JAR task
tasks.withType<Jar> {
    enabled = true // Keep enabled if libraries depend on it, otherwise can be false
}
