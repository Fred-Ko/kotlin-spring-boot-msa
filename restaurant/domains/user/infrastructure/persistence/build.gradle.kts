plugins {
    kotlin("jvm")
    kotlin("plugin.jpa")
    kotlin("plugin.allopen")
    
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

dependencies {
    api(project(":domains:user:domain"))
    implementation(project(":domains:common"))
    implementation(project(":independent:outbox"))
    implementation(project(":domains:user:infrastructure:messaging"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.5")
    runtimeOnly("com.h2database:h2:2.2.224")
    implementation("org.springframework:spring-context:6.1.6")
    implementation("org.springframework:spring-tx:6.1.6")
}
