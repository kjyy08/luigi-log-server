plugins {
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("conventions")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

noArg {
    annotation("jakarta.persistence.Entity")
}

tasks.findByName("bootJar")?.let {
    it.enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

dependencies {
    implementation(libs.spring.boot.starter.core)
    testImplementation(libs.spring.boot.starter.test)
}
