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
		property("sonar.organization", "kjyy08")
		property("sonar.host.url", "https://sonarcloud.io")
		property("sonar.sources", "src/main/kotlin")
		property("sonar.tests", "src/test/kotlin")
		property("sonar.sourceEncoding", "UTF-8")
		property("sonar.language", "kotlin")
		
		// Coverage and Test Reports
		property("sonar.coverage.jacoco.xmlReportPaths", "**/build/reports/jacoco/test/jacocoTestReport.xml")
		property("sonar.junit.reportPaths", "**/build/test-results/test/TEST-*.xml")
		
		// Quality Gate Configuration
		property("sonar.qualitygate.wait", "true")
		
		// Kotlin-specific Rules
		property("sonar.kotlin.detekt.reportPaths", "**/build/reports/detekt/detekt.xml")
		
		// Exclusion Settings
		property("sonar.exclusions", "**/build/**,**/target/**,**/*.gradle.kts,**/gradle/**,**/.gradle/**,**/gradlew,**/gradlew.bat,**/gradle/wrapper/**")
		property("sonar.test.exclusions", "**/build/**,**/target/**")
		property("sonar.cpd.exclusions", "**/build/**,**/target/**")
		
		// Coverage Thresholds
		property("sonar.coverage.exclusions", "**/build/**,**/target/**,**/*Test.kt,**/*Tests.kt,**/*Application.kt")
		property("sonar.coverage.line.threshold", "80.0")
		property("sonar.coverage.branch.threshold", "75.0")
		
		// Security Configuration
		property("sonar.security.hotspots.disable", "false")
		property("sonar.security.hotspots.reviewPriority", "HIGH")
		
		// Quality and Technical Debt Thresholds
		property("sonar.tech_debt.rating", "A")
		property("sonar.duplicated_lines_density", "3.0")
		property("sonar.maintainability.rating", "A")
		property("sonar.reliability.rating", "A")
		property("sonar.security.rating", "A")
		
		// New Code Definition
		property("sonar.newCode.referenceBranch", "main")
	}
}
