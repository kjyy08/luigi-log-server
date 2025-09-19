plugins {
    id("org.sonarqube")
    id("org.jetbrains.kotlinx.kover")
}

subprojects {
    apply(plugin = "org.jetbrains.kotlinx.kover")
}

dependencies {
    subprojects.forEach { subproject ->
        kover(subproject)
    }
}

kover {
    reports {
        total {
            xml {
                title = "Luigi Log Server Coverage Report"
                // SonarQube 호환을 위한 루트 프로젝트 XML 리포트 경로
                xmlFile = layout.buildDirectory.file("reports/kover/coverage.xml").get().asFile
                onCheck = true
            }

            html {
                title = "Luigi Log Server Coverage Report"
                htmlDir = layout.buildDirectory.dir("reports/kover/html").get().asFile
                onCheck = false
                charset = "UTF-8"
            }

            log {
                onCheck = false
                header = "Luigi Log Server Coverage Summary"
                groupBy = kotlinx.kover.gradle.plugin.dsl.GroupingEntityType.APPLICATION
                aggregationForGroup = kotlinx.kover.gradle.plugin.dsl.AggregationType.COVERED_PERCENTAGE
                format = "<entity> coverage: <value>%"
                coverageUnits = kotlinx.kover.gradle.plugin.dsl.CoverageUnit.LINE
            }

            verify {
                // 프로젝트 기본 목표: 80% (리스크 관리 문서 목표, 개발 진행시 적용)
                rule("Project Target Coverage") {
                    minBound(80)
                    disabled = true  // 테스트 작성 완료 후 활성화
                }

                // 브랜치 커버리지 목표: 75% (추후 활성화)
                rule("Branch Coverage Target") {
                    minBound(75, kotlinx.kover.gradle.plugin.dsl.CoverageUnit.BRANCH)
                    disabled = true  // 테스트 작성 완료 후 활성화
                }

                // 도메인 코어 최고 품질: 90% (비즈니스 로직 중요도 최상)
                rule("Domain Core Excellence") {
                    groupBy = kotlinx.kover.gradle.plugin.dsl.GroupingEntityType.CLASS
                    minBound(90)
                    disabled = true  // 도메인 로직 구현 완료 후 활성화
                }

                // 공통 라이브러리 고품질: 85% (재사용성 보장)
                rule("Common Library Quality") {
                    groupBy = kotlinx.kover.gradle.plugin.dsl.GroupingEntityType.CLASS
                    minBound(85)
                    disabled = true  // 라이브러리 테스트 작성 후 활성화
                }

                // 현재 적용 규칙: 최소한의 품질 확보
                rule("Current Minimum Standard") {
                    minBound(0)  // 테스트 작성 시작 단계
                }
            }

            filters {
                excludes {
                    classes("*.*Application*")
                    classes("*.*ApplicationKt")
                    classes("*.*Config*")
                    classes("*.*Dto")
                    classes("*.*Request")
                    classes("*.*Response")
                    classes("*.*Test")
                    classes("*.*Tests")
                    classes("*.*IT")
                    classes("*.*Fixture*")
                }
            }
        }
    }
}

