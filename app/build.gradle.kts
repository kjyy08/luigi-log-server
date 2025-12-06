plugins {
    springBootConventions
    koverReport
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":libs:adapter:persistence:jpa"))
    implementation(project(":libs:adapter:persistence:redis"))
    implementation(project(":libs:adapter:message:spring"))
    implementation(project(":libs:adapter:web"))
    implementation(project(":domain:member:adapter:in:web"))
    implementation(project(":domain:member:adapter:out:persistence:jpa"))
    implementation(project(":domain:member:adapter:out:persistence:redis"))
    implementation(project(":domain:member:adapter:out:auth:jwt"))
    implementation(project(":domain:content"))
    implementation(project(":domain:media"))

    implementation(libs.bundles.spring.boot.web)
    implementation(libs.bundles.spring.boot.data)
    implementation(libs.bundles.spring.boot.security)
    implementation(libs.bundles.flyway)
    implementation(libs.springdoc.openapi)
}

tasks.findByName("bootJar")?.let {
    it.enabled = true
}

tasks.getByName<Jar>("jar") {
    enabled = false
}
