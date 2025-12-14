plugins {
    kotlin("jvm")
    `java-library`
    id("org.jlleitschuh.gradle.ktlint")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

configurations.all {
    exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    exclude(group = "junit", module = "junit")
}

ktlint {
    version.set("1.8.0")
    verbose.set(true)
    android.set(false)

    filter {
        exclude("**/generated/**")
        exclude("**/build/**")
        exclude("**/*.gradle.kts")
    }
}

dependencies {
    implementation(libs.bundles.kotlin.dependencies)
}
