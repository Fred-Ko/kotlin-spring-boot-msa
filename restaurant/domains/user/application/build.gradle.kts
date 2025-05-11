plugins {
    `java-library`
    kotlin("jvm")
    kotlin("plugin.spring")
    id("io.spring.dependency-management")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.3")
    }
}

dependencies {
    implementation("io.github.microutils:kotlin-logging-jvm:4.0.0-beta-2")
    implementation("org.springframework.security:spring-security-crypto:6.4.5")
    api(project(":domains:common:application"))
    api(project(":domains:user:domain"))
    implementation(project(":domains:common:domain"))
    implementation(project(":independent:outbox"))

    implementation("org.springframework:spring-tx") // 트랜잭션 의존성 추가
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.github.resilience4j:resilience4j-spring-boot3:2.2.0")
    implementation("io.github.resilience4j:resilience4j-kotlin:2.2.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "21"
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
