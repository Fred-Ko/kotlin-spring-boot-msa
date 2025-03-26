plugins {
    kotlin("jvm")
    kotlin("kapt")
}

dependencies {
    implementation(project(":domains:common"))
    implementation(project(":domains:user:domain"))

    implementation("org.springframework:spring-context:7.0.0-M3")
    implementation("org.springframework:spring-tx:7.0.0-M3")
    implementation("org.springframework:spring-web:7.0.0-M3")
    implementation("org.mapstruct:mapstruct:1.6.3")

    // MapStruct 어노테이션 프로세서
    kapt("org.mapstruct:mapstruct-processor:1.6.3")

    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.4")
    testImplementation("org.assertj:assertj-core:3.27.3")
}
