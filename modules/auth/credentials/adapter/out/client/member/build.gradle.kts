plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":modules:auth:credentials:domain"))
    implementation(project(":modules:auth:credentials:application"))

    implementation(project(":modules:member:application"))
    implementation(project(":modules:member:domain"))
}
