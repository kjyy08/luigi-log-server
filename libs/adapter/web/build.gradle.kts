plugins {
    springLibraryConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(libs.bundles.spring.boot.web)

    compileOnly(libs.spring.boot.starter.security)
}
