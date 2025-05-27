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

dependencies {
    implementation(project(":domains:common:domain"))
    implementation(project(":domains:common:application"))
    implementation(project(":independent:outbox"))

    implementation("org.springframework.boot:spring-boot-starter-web:3.5.0")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.5.0")

    // implementation("org.springframework.boot:spring-boot-starter-security:3.2.3") // Spring Security 추가
    implementation("org.springframework.security:spring-security-crypto:6.5.0") // PasswordEncoder만 사용
    // JPA는 presentation 레이어에서 직접 사용하지 않지만, 예외 처리를 위해 compileOnly로 추가
    compileOnly("org.springframework.boot:spring-boot-starter-data-jpa:3.5.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.7") // Kotlin Logging 추가

    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.0")
    testImplementation("io.mockk:mockk:1.14.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.0-M3")
    testImplementation("org.assertj:assertj-core:4.0.0-M1")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.0")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.set(listOf("-Xjsr305=strict"))
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
