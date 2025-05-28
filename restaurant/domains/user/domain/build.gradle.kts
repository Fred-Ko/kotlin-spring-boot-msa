plugins {
    id("java-library")
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.jpa") version "2.1.0" // For JPA Entities
    kotlin("plugin.allopen") version "2.1.0" // For JPA Entities
    kotlin("plugin.serialization") version "2.1.0"
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":domains:common:domain"))
    
    // kotlinx.serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.1")
    
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.1.0")
    implementation("com.github.avro-kotlin.avro4k:avro4k-core:2.3.0")
    implementation("jakarta.validation:jakarta.validation-api:3.1.0")
    implementation("com.fasterxml.uuid:java-uuid-generator:5.1.0")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.7")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.0")
    testImplementation("io.mockk:mockk:1.14.2")
    testImplementation("org.assertj:assertj-core:4.0.0-M1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.addAll(listOf("-Xjsr305=strict"))
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }

}

sourceSets {
    main {
        kotlin.srcDirs("src/main/kotlin")
    }
    test {
        kotlin.srcDirs("src/test/kotlin")
    }
}
