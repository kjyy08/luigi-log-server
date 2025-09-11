plugins {
	kotlin("jvm") version "1.9.25" apply false
	kotlin("plugin.spring") version "1.9.25" apply false
	kotlin("plugin.jpa") version "1.9.25" apply false
	id("org.springframework.boot") version "3.5.5" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
	id("org.sonarqube") version "6.3.1.5724"
}

allprojects {
	group = "cloud.luigi99"
	version = "0.0.1-SNAPSHOT"
	
	repositories {
		mavenCentral()
	}
}

sonar {
	properties {
		property("sonar.projectKey", "kjyy08_luigi-log-server")
		property("sonar.projectName", "Luigi Log Server")
		property("sonar.qualitygate.wait", "true")
		property("sonar.scanner.javaOpts", "-Xmx2g -XX:MaxMetaspaceSize=1g")
	}
}
