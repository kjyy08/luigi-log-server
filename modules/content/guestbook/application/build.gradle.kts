plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))
    api(project(":modules:content:guestbook:domain"))
    implementation(project(":modules:member:application"))

    implementation(libs.spring.boot.starter.core)
    implementation(libs.spring.boot.starter.data.jpa)
    testImplementation(libs.bundles.kotlin.test)
}
