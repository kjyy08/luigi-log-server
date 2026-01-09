plugins {
    springLibraryConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":modules:content:guestbook:application"))
    implementation(project(":modules:member:application"))

    implementation(libs.spring.boot.starter.core)
}
