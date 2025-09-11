plugins {
	kotlin("jvm") version "1.9.25" apply false
	kotlin("plugin.spring") version "1.9.25" apply false
	kotlin("plugin.jpa") version "1.9.25" apply false
	id("org.springframework.boot") version "3.5.5" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
	id("org.sonarqube") version "6.3.1.5724"
	id("org.jetbrains.kotlinx.kover") version "0.9.1"
}

allprojects {
	group = "cloud.luigi99"
	version = "0.0.1-SNAPSHOT"
	
	repositories {
		mavenCentral()
	}
}

// 모든 서브프로젝트에 Kover 플러그인 적용
subprojects {
	apply(plugin = "org.jetbrains.kotlinx.kover")
}

// 멀티모듈 Kover 커버리지 집계 설정 (모든 서브프로젝트 자동 포함)
dependencies {
	subprojects.forEach { subproject ->
		kover(subproject)
	}
}

// Kover 커버리지 리포트 설정
kover {
	reports {
		// 전체 프로젝트 커버리지 리포트
		total {
			// XML 리포트 (SonarQube, CI/CD 연동용)
			xml {
				title = "Luigi Log Server Coverage Report"
				xmlFile = layout.buildDirectory.file("reports/kover/coverage.xml").get().asFile
				onCheck = true
			}
			
			// HTML 리포트 (개발자 친화적)
			html {
				title = "Luigi Log Server Coverage Report"
				htmlDir = layout.buildDirectory.dir("reports/kover/html").get().asFile
				onCheck = true
				charset = "UTF-8"
			}
			
			// 로그 리포트 (CI 로그용)
			log {
				onCheck = true
				header = "Luigi Log Server Coverage Summary"
				groupBy = kotlinx.kover.gradle.plugin.dsl.GroupingEntityType.APPLICATION
				aggregationForGroup = kotlinx.kover.gradle.plugin.dsl.AggregationType.COVERED_PERCENTAGE
				format = "<entity> coverage: <value>%"
				coverageUnits = kotlinx.kover.gradle.plugin.dsl.CoverageUnit.LINE
			}
			
			// 커버리지 검증 규칙 (현재 구조만 있는 상태에서는 검증 비활성화)
			verify {
				// 향후 실제 코드 구현 시 활성화 예정
				// rule {
				//     minBound(40) // 점진적으로 80%까지 향상 목표
				// }
			}
			
			// 제외 설정
			filters {
				excludes {
					// Application 엔트리포인트 제외
					classes("*.*Application*")
					classes("*.*ApplicationKt")
					
					// Configuration 클래스 제외 (기본 설정)
					classes("*.*Config*")
					
					// DTO, Entity 단순 데이터 클래스 제외
					classes("*.*Dto")
					classes("*.*Request")
					classes("*.*Response") 
					
					// Test fixture 및 테스트 관련 클래스
					classes("*.*Test*")
					classes("*.*Fixture*")
				}
			}
		}
	}
}

sonar {
	properties {
		property("sonar.scanner.javaOpts", "-Xmx2g -XX:MaxMetaspaceSize=1g")
		// Kover XML 리포트를 SonarQube에 연동
		property("sonar.coverage.jacoco.xmlReportPaths", layout.buildDirectory.file("reports/kover/coverage.xml").get().asFile.absolutePath)
	}
}
