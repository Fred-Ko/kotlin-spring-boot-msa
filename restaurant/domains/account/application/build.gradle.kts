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
}

dependencies {
    implementation("io.github.microutils:kotlin-logging-jvm:4.0.0-beta-2")
    implementation("org.springframework.security:spring-security-crypto:6.5.0")
    implementation(project(":domains:common:domain"))
    implementation(project(":domains:account:domain"))
    implementation(project(":domains:common:application"))
    implementation(project(":independent:outbox"))

    implementation("org.springframework:spring-tx:7.0.0-M5") // 트랜잭션 의존성 추가
    implementation("org.springframework.boot:spring-boot-starter:3.5.0")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.5.0")
    implementation("io.github.resilience4j:resilience4j-spring-boot3:2.3.0")
    implementation("io.github.resilience4j:resilience4j-kotlin:2.3.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.0")
    testImplementation("io.mockk:mockk:1.14.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.0-M3")
    testImplementation("org.assertj:assertj-core:4.0.0-M1")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.addAll(listOf("-Xjsr305=strict"))
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

// Remove explicit task dependencies to avoid circular dependency
tasks.named("compileKotlin") {
    mustRunAfter(tasks.named("processResources"))
}

tasks.named("compileTestKotlin") {
    mustRunAfter(tasks.named("processTestResources"))
}

// Ensure test tasks use JUnit Platform
tasks.withType<Test> {
    useJUnitPlatform()
}

// Make jar task enabled
tasks.withType<Jar> {
    enabled = true
} 