plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(libs.bundles.redis)
}
