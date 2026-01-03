plugins {
    springBootConventions
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":modules:media:domain"))
    api(project(":modules:media:application"))

    // AWS SDK for S3 (Cloudflare R2)
    implementation(libs.bundles.aws.s3)

    implementation(libs.spring.boot.starter.core)

    testImplementation(libs.bundles.kotlin.test)
    testImplementation(libs.spring.boot.starter.test)
}
