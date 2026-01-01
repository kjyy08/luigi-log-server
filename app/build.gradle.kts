plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))

    implementation(project(":modules:member:domain"))
    implementation(project(":modules:member:application"))
    implementation(project(":modules:member:adapter:in:web"))
    implementation(project(":modules:member:adapter:out:persistence:jpa"))

    implementation(project(":modules:auth:token:domain"))
    implementation(project(":modules:auth:token:application"))
    implementation(project(":modules:auth:token:adapter:in:web"))
    implementation(project(":modules:auth:token:adapter:out:persistence:redis"))
    implementation(project(":modules:auth:token:adapter:out:token:jwt"))

    implementation(project(":modules:auth:credentials:domain"))
    implementation(project(":modules:auth:credentials:application"))
    implementation(project(":modules:auth:credentials:adapter:in:web"))
    implementation(project(":modules:auth:credentials:adapter:out:persistence:jpa"))
    implementation(project(":modules:auth:credentials:adapter:out:client:member"))

    implementation(project(":modules:content:domain"))
    implementation(project(":modules:content:application"))
    implementation(project(":modules:content:adapter:in:web"))
    implementation(project(":modules:content:adapter:out:persistence:jpa"))

    implementation(libs.bundles.spring.boot.web)
    implementation(libs.bundles.spring.boot.data)
}

tasks.findByName("bootJar")?.let {
    it.enabled = true
}
