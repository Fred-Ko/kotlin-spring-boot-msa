rootProject.name = "kafka-avro-microservice"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://packages.confluent.io/maven/") }
        maven { url = uri("https://jitpack.io") }
    }
}

include("user-service")
include("order-service")
include("shared-schemas")