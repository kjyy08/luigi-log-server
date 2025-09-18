plugins {
    id("spring-boot")
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