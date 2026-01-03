# Luigi Blog Server

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.21-7F52FF?style=flat-square&logo=kotlin)](https://kotlinlang.org)&nbsp;&nbsp;
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-6DB33F?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)&nbsp;&nbsp;
[![JDK](https://img.shields.io/badge/JDK-21-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)&nbsp;&nbsp;
[![Gradle](https://img.shields.io/badge/Gradle-8.x-02303A?style=flat-square&logo=gradle)](https://gradle.org)

## 📖 프로젝트 소개

**Domain-Driven Design (DDD)** 와 **Hexagonal Architecture**를 기반으로 설계되어,
도메인 로직의 순수성을 보장하고 비즈니스 요구사항의 변화에 유연하게 대응할 수 있습니다.

### 🎯 핵심 가치

- **도메인 중심 설계** - 비즈니스 로직과 인프라 관심사의 완전한 분리
- **확장 가능한 구조** - 멀티 모듈 아키텍처로 독립적인 도메인 진화 지원
- **이벤트 주도** - 도메인 이벤트를 통한 느슨한 결합과 비동기 처리
- **타입 안전성** - Kotlin의 강력한 타입 시스템 활용

---

## 🚀 주요 기능

### 👤 회원 관리
- 회원 가입 및 프로필 관리
- 프로필 이미지 지원
- 기술 스택, 포트폴리오 URL 등 상세 정보 관리

### 🔒 인증 관리
- OAuth2 소셜 로그인 (GitHub)
- JWT 토큰 기반 stateless 인증/인가
- Redis 기반 Refresh Token 관리
- Role 기반 접근 제어

### 📝 콘텐츠 관리
- 마크다운 기반 블로그 포스트 및 포트폴리오 작성/관리
- 콘텐츠 타입 분류 (BLOG, PORTFOLIO)
- 태그 기반 콘텐츠 분류
- 게시 상태 관리 (DRAFT, PUBLISHED, ARCHIVED)

### 📁 미디어 관리
- 파일 업로드 및 저장
- Cloudflare R2 (S3 호환) 스토리지 지원
- 이미지 및 파일 관리

---

## 🏗️ 아키텍처

### Hexagonal Architecture (Port & Adapter)

각 도메인 모듈은 Hexagonal Architecture를 따라 구성됩니다:

```
┌─────────────────────────────────────────────────────────┐
│                     Adapter (In)                        │
│         REST Controllers, Event Listeners               │
├─────────────────────────────────────────────────────────┤
│                   Application Layer                     │
│      Use Cases (Ports), Service Implementations         │
├─────────────────────────────────────────────────────────┤
│                    Domain Layer                         │
│    Aggregate Roots, Entities, Value Objects, Events     │
│              (순수 Kotlin, 프레임워크 독립적)              │
├─────────────────────────────────────────────────────────┤
│                   Adapter (Out)                         │
│    JPA Repositories, Redis, Storage, External APIs      │
└─────────────────────────────────────────────────────────┘
```

**핵심 원칙:**
- **도메인 순수성**: Domain 계층은 프레임워크 의존성 제로
- **의존성 역전**: Application 계층이 Port(인터페이스)를 정의하고, Adapter가 구현
- **독립적 진화**: 각 도메인은 독립적으로 발전 가능

### 멀티 모듈 구조

```
blog-server/
├── libs/                    # 공유 라이브러리
│   ├── common/              # 공통 기본 클래스 (순수 Kotlin)
│   └── adapter/             # 프레임워크 어댑터 (JPA, Redis, Web 등)
│
├── modules/                 # 도메인 모듈 (Bounded Contexts)
│   ├── member/              # 회원 도메인
│   ├── auth/                # 인증 도메인
│   │   ├── token/           # JWT 토큰 관리 (Redis)
│   │   └── credentials/     # OAuth2 인증 (JPA)
│   ├── content/             # 콘텐츠 도메인
│   └── media/               # 미디어 도메인
│
└── app/                     # 애플리케이션 실행 모듈
```

---

## 🛠️ 기술 스택

### Core
- **Kotlin 2.2.21** - JVM 언어
- **Spring Boot 4.0.0** - 애플리케이션 프레임워크

### Persistence
- **PostgreSQL 16** - 메인 데이터베이스
- **Redis** - Refresh Token 저장소
- **Flyway 11.13.2** - 데이터베이스 마이그레이션
- **Cloudflare R2** - S3 호환 객체 스토리지

### Security
- **Spring Security** - 인증/인가
- **OAuth2 Client** - 소셜 로그인
- **JJWT 0.13.0** - JWT 토큰 생성 및 검증

### Testing & Quality
- **Kotest 6.0.7** - 테스트 프레임워크
- **MockK 1.14.5** - Mocking 라이브러리
- **Kover** - 코드 커버리지 (최소 60%)
- **Ktlint** - 코드 스타일 검사

### Documentation
- **SpringDoc OpenAPI 2.8.14** - API 문서 자동 생성

---

## 🚦 시작하기

### 사전 요구사항
- JDK 21
- PostgreSQL 16
- Redis

### 환경변수 설정

애플리케이션 실행 전에 다음 환경변수를 설정해야 합니다.

#### 필수 환경변수

```bash
# 데이터베이스 설정
DATABASE_URL=jdbc:postgresql://localhost:5432/blog
DATABASE_USERNAME=your_db_username
DATABASE_PASSWORD=your_db_password

# JWT 토큰 설정 (최소 256비트 권장)
JWT_SECRET=your-secure-secret-key-minimum-256-bits

# OAuth2 소셜 로그인 (GitHub)
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret

# Cloudflare R2 스토리지 (미디어 파일)
CLOUDFLARE_R2_ACCOUNT_ID=your_cloudflare_account_id
CLOUDFLARE_R2_ACCESS_KEY_ID=your_r2_access_key_id
CLOUDFLARE_R2_SECRET_ACCESS_KEY=your_r2_secret_access_key
CLOUDFLARE_R2_BUCKET_NAME=your_bucket_name
CLOUDFLARE_R2_PUBLIC_URL=https://your-r2-public-url.com
```

#### 선택적 환경변수 (기본값 제공)

```bash
# Redis 설정 (기본값: localhost:6379)
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# 쿠키 설정
APP_COOKIE_SECURE=true
APP_COOKIE_DOMAIN=localhost
APP_COOKIE_SAME_SITE=None

# 리다이렉트 URL 설정
APP_REDIRECT_BASE_URL=http://localhost:3000
APP_REDIRECT_SUCCESS_END_POINT=/login
APP_REDIRECT_ERROR_END_POINT=/login
```

#### 프로파일별 설정

프로파일을 지정하여 애플리케이션을 실행할 수 있습니다:
- `local` - 로컬 개발 환경 (기본값)
- `dev` - 개발 서버 환경
- `prod` - 프로덕션 환경

```bash
# 프로파일 지정 방법
./gradlew bootRun --args='--spring.profiles.active=dev'

# 또는 환경변수로 설정
SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun
```

### 빌드 및 실행

```bash
# 전체 빌드
./gradlew clean build

# 애플리케이션 실행
./gradlew bootRun
```

### API 문서

애플리케이션 실행 후 Swagger UI에서 API를 확인할 수 있습니다:

**http://localhost:8080/swagger-ui.html**

---

## 📊 코드 품질

```bash
# 테스트 실행
./gradlew test

# 특정 모듈 테스트
./gradlew :modules:member:domain:test

# 코드 스타일 검사
./gradlew ktlintCheck

# 코드 스타일 자동 수정
./gradlew ktlintFormat

# 커버리지 리포트 생성
./gradlew koverHtmlReport

# 커버리지 검증 (최소 60%)
./gradlew koverVerify

# 전체 검사 (테스트 + 린트 + 커버리지)
./gradlew check
```

---

## 📚 프로젝트 구조 및 규칙

### Convention Plugins (buildSrc)

프로젝트는 빌드 설정을 표준화하기 위해 Convention Plugins를 사용합니다:

- **`conventions`** - 기본 설정 (JDK 21, Kotlin, Ktlint)
- **`spring-library-conventions`** - Spring Library 모듈용 설정
- **`spring-boot-conventions`** - Spring Boot 모듈용 설정
- **`kover`** - 코드 커버리지 설정 (최소 60%)

### 의존성 관리

모든 라이브러리 버전은 `gradle/libs.versions.toml`에서 중앙 관리됩니다:

```kotlin
dependencies {
    implementation(libs.bundles.spring.boot.web)      // Spring Web, Validation, Actuator, Swagger
    implementation(libs.bundles.spring.boot.data)     // JPA, Flyway, PostgreSQL
    implementation(libs.bundles.spring.boot.security) // Spring Security, OAuth2
    implementation(libs.bundles.jwt)                  // JJWT
    implementation(libs.bundles.redis)                // Redis
}
```

### DDD 패턴

- **Aggregate Root**: `AggregateRoot<T>` 상속
- **Entity**: `DomainEntity<T>` 상속
- **Value Object**: `@JvmInline value class` 사용 권장
- **Domain Event**: 도메인 이벤트로 부수 효과 표현
- **Mapper Pattern**: JPA Entity ↔ Domain Model 변환
