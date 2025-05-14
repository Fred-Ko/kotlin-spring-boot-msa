plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("plugin.allopen")
    id("com.github.davidmc24.gradle.plugin.avro")
}

avro {
    // 옵션 제거: createSetters, fieldVisibility는 공식 확장에 없음
}

sourceSets {
    main {
        resources {
            srcDir("src/main/resources/avro")
        }
    }
}


allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    implementation(project(":domains:common:domain"))
    implementation(project(":domains:common:application"))
    
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.3") // JPA 어노테이션 사용 목적
    implementation("org.springframework.kafka:spring-kafka:3.1.1")
    implementation("org.apache.avro:avro:1.11.3")
    implementation("io.confluent:kafka-avro-serializer:7.6.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.3")
    testImplementation("org.springframework.kafka:spring-kafka-test:3.1.1")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("org.testcontainers:testcontainers:1.19.6")
    testImplementation("org.testcontainers:junit-jupiter:1.19.6")
    testImplementation("org.testcontainers:kafka:1.19.6")
}
