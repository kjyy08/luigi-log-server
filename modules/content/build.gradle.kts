plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":libs:adapter:persistence:jpa"))
    implementation(project(":libs:adapter:message:spring"))
    implementation(project(":libs:adapter:web"))

    implementation(libs.bundles.spring.boot.web)
}
