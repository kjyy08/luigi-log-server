plugins {
    springLibraryConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(libs.spring.boot.starter.core)
}
