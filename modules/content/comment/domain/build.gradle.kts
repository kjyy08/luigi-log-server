plugins {
    conventions
}

dependencies {
    api(project(":libs:common"))
    api(project(":modules:member:domain"))
    api(project(":modules:content:post:domain"))
    testImplementation(libs.bundles.kotlin.test)
}
