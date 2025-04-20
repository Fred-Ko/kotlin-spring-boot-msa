plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("kapt")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.2"))
    implementation(project(":domains:common"))
    // implementation(project(":shared:outbox:infrastructure")) // 순환 의존성 방지 위해 주석 처리

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.slf4j:slf4j-api")
    implementation("org.slf4j:slf4j-simple")
    implementation("org.springframework.boot:spring-boot-starter-web")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
