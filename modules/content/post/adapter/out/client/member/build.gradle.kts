plugins {
    springLibraryConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":modules:content:post:application"))
    implementation(project(":modules:member:application"))

    implementation(libs.spring.boot.starter.core)
}
