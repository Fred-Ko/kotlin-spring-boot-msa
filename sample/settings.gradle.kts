rootProject.name = "json-kafka-demo"

pluginManagement {
    repositories {
        maven { url = uri("https://packages.confluent.io/maven/") } 
        gradlePluginPortal()
        google() // Re-adding google() repository
        mavenCentral()
    }
}
