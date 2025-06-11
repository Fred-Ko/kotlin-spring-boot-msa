plugins {
    `java-library`
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    id("io.spring.dependency-management")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.0")
    }
    dependencies {
        dependency("org.jetbrains.kotlin:kotlin-reflect:2.1.0")
        dependency("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
        dependency("io.github.resilience4j:resilience4j-spring-boot3:2.3.0")
        dependency("io.github.resilience4j:resilience4j-kotlin:2.3.0")
        dependency("org.springframework.boot:spring-boot-starter:3.5.0")
        dependency("org.springframework.boot:spring-boot-starter-validation:3.5.0")
        dependency("org.springframework.boot:spring-boot-starter-test:3.5.0")
        dependency("io.mockk:mockk:1.14.2")
        dependency("org.junit.jupiter:junit-jupiter:5.13.0-M3")
        dependency("org.assertj:assertj-core:4.0.0-M1")
    }
}

dependencies {
    implementation(project(":domains:common:domain"))
    
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
    implementation("io.github.resilience4j:resilience4j-spring-boot3:2.3.0")
    implementation("io.github.resilience4j:resilience4j-kotlin:2.3.0")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.0")
    testImplementation("io.mockk:mockk")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.addAll(listOf("-Xjsr305=strict"))
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

tasks.jar {
    enabled = true
}
