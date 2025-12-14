plugins {
    conventions
    `java-library`
}

dependencies {
    api(project(":libs:common"))
    api(project(":modules:member:domain"))
    testImplementation(libs.bundles.kotlin.test)
}
