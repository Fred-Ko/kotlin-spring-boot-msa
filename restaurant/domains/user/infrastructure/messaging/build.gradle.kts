plugins {
    kotlin("jvm")
    id("java-library")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
    id("com.github.davidmc24.gradle.plugin.avro")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

sourceSets {
    main {
        java.srcDirs("src/main/java", "build/generated-src/avro/main/java")
        kotlin.srcDirs("src/main/kotlin", "build/generated-src/avro/main/java")
    }
}

dependencies {
    api(project(":domains:common:domain"))
    api(project(":domains:common:infrastructure"))
    api(project(":domains:user:domain"))
    api(project(":independent:outbox"))
    
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
    
    // Add explicit slf4j-api dependency for MDC
    implementation("org.slf4j:slf4j-api:2.0.16")
    
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

// Ensure generateAvro task runs before compile tasks
tasks.named<JavaCompile>("compileJava") {
    dependsOn(tasks.named("generateAvroJava"))
}

tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") {
    dependsOn(tasks.named("generateAvroJava"))
}
