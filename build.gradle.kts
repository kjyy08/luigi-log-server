group = "cloud.luigi99"
version = "0.0.1-SNAPSHOT"
description = "Luigi Blog Server"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    tasks.withType<Jar> {
        archiveBaseName.set(project.path.replace(":", "-").substring(1))
    }
}
