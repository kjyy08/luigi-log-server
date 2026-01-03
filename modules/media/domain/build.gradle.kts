plugins {
    conventions
}

dependencies {
    api(project(":libs:common"))
    testImplementation(libs.bundles.kotlin.test)
}
