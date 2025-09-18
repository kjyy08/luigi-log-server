// Spring Boot 관련 버전 정의
val springBootVersion = "3.5.5"
val springSecurityVersion = "6.4.0"
val springFrameworkVersion = "6.2.0"
val jacksonVersion = "2.18.1"
val jjwtVersion = "0.12.3"

plugins {
    id("kotlin-base")
    id("test")
    kotlin("plugin.spring")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

allOpen {
    annotation("org.springframework.stereotype.Component")
    annotation("org.springframework.stereotype.Service")
    annotation("org.springframework.stereotype.Repository")
    annotation("org.springframework.boot.context.properties.ConfigurationProperties")
}

noArg {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
    }
}

// 라이브러리 모듈을 위한 설정: bootJar 비활성화, 일반 jar 활성화
tasks.named("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
    archiveClassifier.set("")
}

dependencies {
    // 자동으로 common-domain 의존성 제공 (모든 Spring Boot 모듈에서 필요)
    implementation(project(Modules.Libs.commonDomain.path))

    implementation("org.springframework.boot:spring-boot-starter:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-configuration-processor:$springBootVersion")

    // Spring Boot 관련 의존성들
    implementation("org.springframework.boot:spring-boot-starter-web:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-security:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-validation:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-actuator:$springBootVersion")

    // Spring Security
    implementation("org.springframework.security:spring-security-core:$springSecurityVersion")

    // Jackson for JSON processing
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testImplementation("org.springframework.security:spring-security-test:$springSecurityVersion")
}