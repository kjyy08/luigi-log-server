plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":modules:auth:token:domain"))
    implementation(project(":modules:auth:token:application"))
    implementation(project(":modules:member:domain"))

    implementation(libs.bundles.jwt)
}
