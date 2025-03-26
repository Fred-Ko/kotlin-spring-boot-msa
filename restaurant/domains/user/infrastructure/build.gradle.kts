plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

dependencies {
    implementation(project(":domains:user:domain"))
    implementation(project(":domains:common"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.mapstruct:mapstruct:1.6.3")

    // MapStruct 어노테이션 프로세서 - Kotlin에서는 kapt 사용
    kapt("org.mapstruct:mapstruct-processor:1.6.3")

    // H2 데이터베이스
    runtimeOnly("com.h2database:h2:2.3.232")

    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
