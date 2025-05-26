plugins {
    `java-library`
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    kotlin("plugin.jpa") version "2.1.0" // For JPA Entities
    kotlin("plugin.allopen") version "2.1.0" // For JPA Entities
    kotlin("plugin.serialization") version "2.1.0"
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

tasks.named("bootJar") {
    enabled = false
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.0")
    }
}

dependencies {
    implementation(project(":domains:common:domain"))
    implementation(project(":domains:common:application"))
    
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.5.0") // JPA 어노테이션 사용 목적
    implementation("org.springframework.kafka:spring-kafka:4.0.0-M2")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.0")
    testImplementation("org.springframework.kafka:spring-kafka-test:4.0.0-M2")
    testImplementation("io.mockk:mockk:1.14.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.0-M3")
    testImplementation("org.assertj:assertj-core:4.0.0-M1")
    testImplementation("org.testcontainers:testcontainers:1.21.0")
    testImplementation("org.testcontainers:junit-jupiter:1.21.0")
    testImplementation("org.testcontainers:kafka:1.21.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.set(listOf("-Xjsr305=strict"))
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
