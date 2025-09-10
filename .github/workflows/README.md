# GitHub Actions CI/CD Workflows

Luigi Log Server 프로젝트의 GitHub Actions 워크플로우 설정에 대한 설명입니다.

## 🚀 CI/CD Pipeline

### ci.yml - 메인 CI 파이프라인

**트리거**
- `main`, `develop` 브랜치 푸시
- `main`, `develop` 브랜치로의 Pull Request (opened, synchronize, reopened)

**주요 특징**
- ⚡ **최적화된 빌드 시간**: 목표 5분 이내 완료
- 🔄 **병렬 처리**: Gradle 병렬 빌드 활용
- 💾 **지능적 캐싱**: Gradle 의존성 및 빌드 캐시
- 🧪 **포괄적 테스트**: 모든 모듈의 단위 테스트
- 📊 **상세한 리포팅**: 테스트 결과 및 커버리지 보고서
- 🔒 **보안 스캔**: 의존성 취약점 검사
- ✨ **코드 품질**: 정적 분석 및 품질 검사

### 워크플로우 단계

#### 1. 테스트 및 빌드 (test)
```yaml
- Gradle wrapper 검증
- JDK 17 설정 (Temurin)
- Gradle 캐시 설정
- 테스트 실행 (병렬)
- 프로젝트 빌드
- 테스트 결과 업로드
```

#### 2. 보안 스캔 (security-scan) - PR만
```yaml
- 의존성 취약점 검사
- 보안 리포트 생성
```

#### 3. 코드 품질 (code-quality) - PR만  
```yaml
- 정적 코드 분석
- 품질 메트릭 생성
```

#### 4. 빌드 성공 확인 (build-success)
```yaml
- 모든 단계 결과 검증
- PR 상태 업데이트
```

#### 5. Docker 이미지 빌드 및 푸시 (docker-build-push) - main 브랜치만
```yaml
- Spring Boot JAR 빌드
- Docker 메타데이터 추출
- 멀티플랫폼 Docker 이미지 빌드 (amd64, arm64)
- GitHub Container Registry 푸시
```

## 🔧 성능 최적화

### Gradle 설정
- **병렬 빌드**: `--parallel` 옵션으로 모듈 병렬 처리
- **빌드 캐시**: `--build-cache`로 반복 빌드 최적화  
- **Daemon**: Gradle daemon으로 JVM 재시작 오버헤드 제거
- **메모리 할당**: CI 환경에 최적화된 JVM 메모리 설정

### 캐싱 전략
```yaml
# Gradle 캐시
~/.gradle/caches
~/.gradle/wrapper
.gradle

# 캐시 키: 빌드 파일 해시 기반
key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
```

### JVM 최적화
```bash
GRADLE_OPTS: "-Xmx2g -XX:+UseG1GC -XX:+UseStringDeduplication"
```

## 📊 테스트 리포팅

### 업로드되는 아티팩트
- **테스트 결과**: `**/build/reports/tests/`
- **테스트 결과 XML**: `**/build/test-results/`
- **빌드 아티팩트**: `**/build/libs/*.jar`
- **보안 리포트**: `build/reports/`
- **코드 품질 리포트**: `**/build/reports/`
- **Docker 이미지**: GitHub Container Registry (main 브랜치만)

### PR 통합
- 테스트 결과가 PR 코멘트로 자동 추가
- 실패한 테스트에 대한 상세 정보 제공
- 빌드 상태가 PR 상태로 표시

## 🔒 보안 고려사항

### 권한 설정
```yaml
permissions:
  contents: read        # 소스 코드 읽기
  checks: write        # 체크 결과 쓰기
  pull-requests: write # PR 코멘트 쓰기
```

### 보안 스캔
- Gradle 의존성 취약점 검사
- 보안 이슈 발견 시 리포트 생성
- 취약점이 있어도 빌드 중단하지 않음 (경고만)

## 🛠 로컬 개발

### CI와 동일한 환경에서 테스트
```bash
# 전체 테스트 실행
./gradlew test --parallel --build-cache --continue

# 빌드 실행  
./gradlew assemble --parallel --build-cache

# 코드 품질 검사
./gradlew check --parallel --continue
```

### Gradle 성능 설정
- `gradle.properties`: 로컬 개발용 최적화 설정
- `.github/workflows/gradle.properties`: CI 전용 설정

## 🚨 문제 해결

### 빌드 실패 시
1. **의존성 문제**: 캐시 클리어 후 재시도
2. **메모리 부족**: JVM 힙 크기 조정
3. **테스트 실패**: 개별 모듈별 테스트로 원인 파악

### 성능 이슈 시
1. **빌드 시간 초과**: 타임아웃 조정 (현재 15분)
2. **캐시 미스**: 캐시 키 확인 및 파일 패턴 검토
3. **병렬 처리 문제**: 순차 실행으로 전환

## 📈 모니터링

### 주요 메트릭
- 빌드 시간 (목표: < 5분)
- 테스트 성공률 (목표: 100%)
- 캐시 적중률 (목표: > 90%)
- 의존성 취약점 수

### 로그 확인
GitHub Actions 탭에서 각 단계별 상세 로그 확인 가능

## 🔄 업데이트 가이드

### 의존성 업데이트
- Action 버전: 정기적으로 최신 버전으로 업데이트
- JDK 버전: 프로젝트 요구사항에 맞춰 조정
- Gradle 버전: 호환성 확인 후 업그레이드

### 워크플로우 수정
- 변경 사항은 별도 브랜치에서 테스트
- PR을 통해 검토 후 병합
- 중요한 변경사항은 문서 업데이트 필수

## 🐳 Docker 이미지 배포

### 자동 배포 조건
- **main 브랜치 푸시 시에만 실행**
- 테스트 단계 성공 후 실행
- GitHub Container Registry (ghcr.io)에 푸시

### 이미지 태그 전략
```yaml
# 생성되는 태그들
ghcr.io/{owner}/{repo}:latest          # main 브랜치 최신
ghcr.io/{owner}/{repo}:main            # 브랜치 이름
ghcr.io/{owner}/{repo}:main-{sha}      # 브랜치-커밋SHA
ghcr.io/{owner}/{repo}:{timestamp}     # 빌드 타임스탬프
```

### 멀티플랫폼 지원
- **linux/amd64**: Intel/AMD 64비트 시스템
- **linux/arm64**: ARM 64비트 시스템 (Apple M1/M2, ARM 서버)

### 이미지 최적화
- **멀티스테이지 빌드**: 빌드 도구와 런타임 분리
- **경량 베이스 이미지**: OpenJDK 17 JRE Slim
- **레이어 캐싱**: GitHub Actions 캐시 활용
- **보안 강화**: 비특권 사용자로 실행

### 사용 방법
```bash
# 최신 이미지 실행
docker run -p 8080:8080 ghcr.io/{owner}/luigi-log-server:latest

# 특정 태그 실행
docker run -p 8080:8080 ghcr.io/{owner}/luigi-log-server:main-{sha}
```

### 환경변수
- `SPRING_PROFILES_ACTIVE=docker`: Docker 환경용 프로파일
- `JAVA_OPTS`: JVM 최적화 옵션 사전 설정
- `TZ=Asia/Seoul`: 타임존 설정