plugins {
    kotlin("jvm")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
    id("com.github.davidmc24.gradle.plugin.avro")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation(project(":domains:common:domain"))
    implementation(project(":domains:common:application"))
    implementation(project(":domains:common:infrastructure"))
    implementation(project(":domains:user:domain"))
    implementation(project(":domains:user:application"))
    implementation(project(":independent:outbox"))
    
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    
    // Spring
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework:spring-tx")
    
    // Kafka
    implementation("org.springframework.kafka:spring-kafka")
    
    // Kafka & Avro
    implementation("org.apache.kafka:kafka-clients")
    implementation("org.apache.avro:avro")
    implementation("io.confluent:kafka-avro-serializer:7.6.3")
    
    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.testcontainers:kafka:1.20.2")
    testImplementation("org.testcontainers:junit-jupiter:1.20.2")
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

// Configure Avro plugin
avro {
    setCreateSetters(false)
    setFieldVisibility("PRIVATE")
    setOutputCharacterEncoding("UTF-8")
    stringType = "String"
}
