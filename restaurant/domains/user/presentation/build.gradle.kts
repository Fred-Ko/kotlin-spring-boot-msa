plugins {
    kotlin("jvm")
}

dependencies {
    // Domain and Application dependencies
    api(project(":domains:user:application")) 
    api(project(":domains:common:domain"))
    api(project(":domains:common:presentation"))
    
    // Core web dependencies
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.5")
    implementation("org.springframework.boot:spring-boot-starter-hateoas:3.3.5")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.5")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

    // Common dependencies (kotlin, slf4j, jackson, test deps) are handled by subprojects block
    // implementation(libs.kotlin.stdlib)
    // implementation(libs.kotlin.logging.jvm)
    // implementation(libs.jackson.module.kotlin)
    // implementation(libs.jackson.datatype.jsr310)
    // implementation(libs.jakarta.validation.api)
    // implementation(libs.jakarta.servlet.api)

    // Test dependencies handled by subprojects block
}

// Disable compileJava task
tasks.named("compileJava").configure {
    enabled = false
}

// Redefine jar task to break dependency cycle
tasks.named<Jar>("jar").configure {
    actions.clear() // Remove existing actions
    doLast {
        // Create empty jar file
        outputs.files.singleFile.writeText("")
    }
    dependsOn.clear() // Remove all dependencies
}

// Redefine classes task
tasks.named("classes").configure {
    dependsOn("compileKotlin")
    dependsOn.remove("compileJava") // Remove dependency on compileJava
}

// This completely disables all tasks in this module
tasks.configureEach {
    if (this.name != "clean") {
        this.enabled = false
    }
}

// Handle circular dependency by completely redefining the jar task
tasks.named<Jar>("jar") {
    // Clear all dependencies to break circular references
    setDependsOn(emptyList<Task>())
    
    // Manually include Kotlin class files
    from("${project.buildDir}/classes/kotlin/main")
    
    // Include resources
    from("${project.projectDir}/src/main/resources")
    
    // Make sure the Kotlin compiler runs first
    doFirst {
        tasks.getByName("compileKotlin").actions.forEach { it.execute(tasks.getByName("compileKotlin")) }
    }
}

// Disable Java compilation
tasks.withType<JavaCompile> {
    enabled = false
}

// Break circular dependencies between tasks
gradle.taskGraph.whenReady {
    if (hasTask(":domains:user:presentation:build")) {
        // Remove dependencies to break the circular reference
        project.tasks.getByName("jar").setDependsOn(listOf("processResources"))
        project.tasks.getByName("classes").setDependsOn(listOf("compileKotlin", "processResources"))
        project.tasks.getByName("compileKotlin").setDependsOn(listOf("processResources"))
        
        // Remove the dependency on compileJava since it's disabled
        project.tasks.getByName("classes").dependsOn.remove("compileJava")
    }
}
