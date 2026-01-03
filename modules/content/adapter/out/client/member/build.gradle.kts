plugins {
    springLibraryConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":modules:content:application"))
    implementation(project(":modules:member:application"))

    implementation(libs.spring.boot.starter.core)
}
