plugins {
    id("spring-boot")
}

// 애플리케이션 모듈: bootJar 활성화, 일반 jar 비활성화
tasks.named("bootJar") {
    enabled = true
}

tasks.named("jar") {
    enabled = false
}

dependencies {
    // Common modules
    implementation(project(":libs:common-domain"))
    implementation(project(":libs:common-infrastructure"))
    implementation(project(":libs:common-web"))

    // Service modules
    implementation(project(":service:user:core"))
    implementation(project(":service:user:adapter-in"))
    implementation(project(":service:user:adapter-out"))

    implementation(project(":service:content:core"))
    implementation(project(":service:content:adapter-in"))
    implementation(project(":service:content:adapter-out"))

    implementation(project(":service:search:core"))
    implementation(project(":service:search:adapter-in"))
    implementation(project(":service:search:adapter-out"))

    implementation(project(":service:analytics:core"))
    implementation(project(":service:analytics:adapter-in"))
    implementation(project(":service:analytics:adapter-out"))

    // Database (spring-boot.gradle.kts에서 버전 관리)
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2") // For testing
    
    // Test dependencies
    testImplementation("org.springframework.security:spring-security-test")
}