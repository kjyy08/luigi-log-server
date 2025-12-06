plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(libs.spring.boot.starter.data.jpa)
}
