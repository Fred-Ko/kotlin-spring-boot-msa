plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":domains:common:domain"))
    implementation(project(":domains:common:application"))
    implementation(project(":domains:common:infrastructure"))
    implementation(project(":domains:common:presentation"))
    implementation(project(":domains:user:domain"))
    implementation(project(":domains:user:application"))
    implementation(project(":domains:user:infrastructure:persistence"))
    implementation(project(":domains:user:infrastructure:messaging"))
    implementation(project(":domains:user:presentation"))
    implementation(project(":independent:outbox"))

    implementation("org.springframework.boot:spring-boot-starter-web:3.2.3")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.2.3")
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.2.3")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.3")
    implementation("org.springframework.kafka:spring-kafka:3.1.1")
    implementation("org.flywaydb:flyway-core:9.22.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")

    runtimeOnly("org.postgresql:postgresql:42.7.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.3")
    testImplementation("org.springframework.kafka:spring-kafka-test:3.1.1")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("org.testcontainers:testcontainers:1.19.6")
    testImplementation("org.testcontainers:junit-jupiter:1.19.6")
    testImplementation("org.testcontainers:postgresql:1.19.6")
    testImplementation("org.testcontainers:kafka:1.19.6")
}

tasks.bootJar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
