plugins {
    id("java-library")
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.jpa") version "2.1.0"
    kotlin("plugin.allopen") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("io.spring.dependency-management")
    id("org.springframework.boot") version "3.5.0"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.0")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.5.0")
    implementation("jakarta.validation:jakarta.validation-api:3.1.1")
    implementation("com.fasterxml.uuid:java-uuid-generator:5.1.0")

    testImplementation("io.mockk:mockk:1.14.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.0-M3")
    testImplementation("org.assertj:assertj-core:4.0.0-M1")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.set(listOf("-Xjsr305=strict"))
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

tasks.jar {
    enabled = true
}
tasks.bootJar {
    enabled = false
}
