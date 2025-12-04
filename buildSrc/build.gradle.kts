plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.spring.boot.gradle.plugin)
    implementation(libs.spring.dependency.management.plugin)
    implementation(libs.kotlin.plugin.spring)
    implementation(libs.kotlin.plugin.jpa)
    implementation(libs.kover.gradle.plugin)
    implementation(libs.ktlint.gradle.plugin)
}
