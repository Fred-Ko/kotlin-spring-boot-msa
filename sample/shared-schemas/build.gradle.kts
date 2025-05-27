
plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.serialization") version "1.9.24"
    id("com.github.imflog.kafka-schema-registry-gradle-plugin") version "2.0.0"
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

tasks.register<JavaExec>("generateAvroSchemas") {
    group = "Avro"
    description = "Kotlin 데이터 클래스로부터 Avro 스키마 자동 생성"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.example.schemas.GenerateSchemasKt")
}

dependencies {
    // Kafka
    implementation("org.apache.kafka:kafka-clients:3.7.0")
    
    // Avro
    implementation("org.apache.avro:avro:1.11.3")
    
    // Avro4k for Kotlin serialization
    implementation("com.github.avro-kotlin.avro4k:avro4k-core:2.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.3")
    
    // Schema Registry
    implementation("io.confluent:kafka-schema-registry-client:7.6.1")
    implementation("io.confluent:kafka-avro-serializer:7.6.1")
    
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    
    // Spring
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework:spring-context")
    implementation("org.springframework.kafka:spring-kafka")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.testcontainers:kafka:1.19.7")
    testImplementation("org.testcontainers:junit-jupiter:1.19.7")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {
    enabled = true
}

tasks.findByName("bootJar")?.enabled = false

// Configure schema registry plugin for this module
schemaRegistry {
    url.set("http://localhost:8081")

    register {
        // Specify the schema type correctly with subject name and file path
        subject("user-events-value", file("src/main/avro/user-event.avsc").absolutePath, "AVRO")
        subject("order-events-value", file("src/main/avro/order-event.avsc").absolutePath, "AVRO")
    }
    compatibility {
        // Specify the schema type correctly with subject name and file path
        subject("user-events-value", file("src/main/avro/user-event.avsc").absolutePath, "AVRO")
        subject("order-events-value", file("src/main/avro/order-event.avsc").absolutePath, "AVRO")
    }
}
