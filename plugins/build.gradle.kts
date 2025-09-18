plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.25")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.5.5")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.6")
    implementation("org.jetbrains.kotlin:kotlin-allopen:1.9.25")
    implementation("org.jetbrains.kotlin:kotlin-noarg:1.9.25")
    implementation("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:6.3.1.5724")
    implementation("org.jetbrains.kotlinx:kover-gradle-plugin:0.9.1")
}