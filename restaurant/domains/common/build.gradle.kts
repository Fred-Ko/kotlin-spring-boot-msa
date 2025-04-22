plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    // 필수 Spring Framework 의존성만 유지
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-hateoas:3.3.2")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0") // OptimisticLockException 사용 위해 추가

    // 다른 모듈의 Exception/Error 참조를 위한 의존성 추가
    api(project(":domains:user:domain")) // UserDomainException 등 참조
    api(project(":domains:account:domain")) // AccountDomainException 등 참조
    api(project(":independent:outbox:infrastructure")) // OutboxException 등 참조

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
}
