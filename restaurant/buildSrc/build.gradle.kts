plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // JSON Schema generation dependencies
    implementation("com.fasterxml.jackson.module:jackson-module-jsonSchema:2.19.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.0")
    implementation("io.github.classgraph:classgraph:4.8.175")
    
    // kotlinx.serialization dependencies
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.1")
    implementation("org.jsonschema2pojo:jsonschema2pojo-gradle-plugin:1.2.1")
} 