plugins {
    // kotlin("jvm") // Provided by subprojects block
    `java-library`
    // Common plugins (jvm, java-library) applied via subprojects
}

dependencies {
    // Keep specific API dependency
    api(project(":domains:common"))

    // Common dependencies (kotlin, slf4j, test deps) are handled by subprojects block
}

// REMOVED: Comment about Jar task configuration
