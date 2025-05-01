plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("plugin.allopen")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

dependencies {
    api(project(":domains:user:domain"))
    api(project(":domains:common:domain"))
api(project(":domains:common:infrastructure"))
    api(project(":independent:outbox"))
    implementation(project(":domains:user:infrastructure:messaging"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.5")
    runtimeOnly("com.h2database:h2:2.2.224")
    implementation("org.springframework:spring-context:6.1.6")
    implementation("org.springframework:spring-tx:6.1.6")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    runtimeOnly("org.postgresql:postgresql:42.7.3")
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
}
