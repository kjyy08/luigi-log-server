plugins {
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("conventions")
    id("io.spring.dependency-management")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${libs.versions.springBoot.get()}")
    }
}
