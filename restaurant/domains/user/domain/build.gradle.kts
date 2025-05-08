plugins { kotlin("jvm") }

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":domains:common:domain"))
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:2.0.20"))
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")

    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.mockk:mockk:1.13.13")
}

tasks.withType<Test>().configureEach { useJUnitPlatform() }
