# luigi-log-server AGENTS.md

## 프로젝트

- repo: `kjyy08/luigi-log-server`
- root project: `blog-server`
- group: `cloud.luigi99`
- 역할: luigi-log 블로그 백엔드 REST API
- stack: Kotlin 2.2.x, JDK 21, Spring Boot 4, Gradle Kotlin DSL, PostgreSQL, Redis, Flyway, Cloudflare R2/S3 호환 스토리지
- 구조: Gradle multi-module + DDD/Hexagonal 스타일

> 기존 `CLAUDE.md`는 오래된/예제성 컨텍스트가 섞일 수 있으니 신뢰하지 말고, 실제 파일(`settings.gradle.kts`, `buildSrc`, 각 module source, `application*.yml`) 기준으로 판단한다.

## 주요 경로

```txt
app/
  src/main/kotlin/cloud/luigi99/blog/app/BlogServerApplication.kt
  src/main/resources/application.yml
  src/main/resources/application-{local,dev,prod}.yml
  src/main/resources/db/migration/V*.sql

libs/common/
  # AggregateRoot, DomainEntity, ValueObject, DomainEvent, Repository, BusinessException 등 공통 도메인/애플리케이션 원시 타입

libs/adapter/
  persistence/jpa      # JPA 공통 엔티티/설정
  persistence/redis    # Redis 공통 엔티티/설정
  message/spring       # Spring 기반 domain event publisher/context
  web                  # CommonResponse, GlobalExceptionHandler, Cookie/Swagger 공통

modules/
  member/
  auth/token/
  auth/credentials/
  content/post/
  content/comment/
  content/guestbook/
  media/
```

## 모듈 패턴

대부분 bounded context는 아래 계층을 따른다.

```txt
<context>/
├── domain/                  # 순수 도메인. Spring/JPA/Redis 의존 금지
├── application/             # use case, command/query service, port
└── adapter/
    ├── in/web/              # controller, request/response DTO, API 인터페이스
    ├── in/event/            # Spring event listener, 필요 시
    └── out/
        ├── persistence/jpa/ # JPA entity/repository/mapper/adapter
        ├── persistence/redis/
        ├── client/...       # 다른 context 접근용 client adapter
        └── storage/r2/      # media storage adapter
```

## 작업 위치

- 모든 backend 작업은 이 repo 루트에서 한다.

```bash
cd /home/luigi/.hermes/profiles/boksili/home/workspace/kjyy08/luigi-log/server
```

- `luigi-log/` 상위 디렉토리는 관리 컨텍스트일 뿐이고 git repo가 아니다.
- client/API 계약 변경이 있으면 `../client` 영향도 같이 본다.
- env, DB, Redis, 이미지/포트 변경은 `../gitops` 영향도 같이 본다.

## 명령

```bash
# 주의: gradlew 실행권한(mode)은 임의로 바꾸지 않는다.
# 실행권한이 없는 repo 상태면 chmod 대신 bash ./gradlew ... 형태로 실행한다.

# 전체 테스트
bash ./gradlew test

# 전체 검증
bash ./gradlew check

# 전체 빌드
bash ./gradlew clean build

# 실행 JAR 검증
bash ./gradlew :app:bootJar

# 로컬 실행
bash ./gradlew :app:bootRun

# 스타일
bash ./gradlew ktlintCheck
bash ./gradlew ktlintFormat

# 커버리지
bash ./gradlew koverHtmlReport
bash ./gradlew koverVerify
```

CI 스타일 검증이 필요하면 아래 조합을 우선한다.

```bash
bash ./gradlew test check :app:bootJar --parallel --build-cache --continue --stacktrace
```

## 아키텍처 규칙

- package는 기존 `cloud.luigi99.blog` 하위를 유지한다.
- `app` module은 실행/조립 계층이다. 도메인 로직을 넣지 않는다.
- `domain` module은 framework 독립을 유지한다.
  - 허용: Kotlin, `libs/common` 도메인 원시 타입
  - 금지: Spring annotation, JPA annotation, Redis, HTTP, storage SDK 직접 의존
- application 계층은 use case와 port 중심으로 둔다.
  - inbound: `application/port/in/...`
  - outbound: `application/port/out/...`
  - 구현: `application/service/...`
- adapter 계층은 외부 기술을 담당한다.
  - web controller/API DTO: `adapter/in/web`
  - event listener: `adapter/in/event`
  - JPA/Redis/storage/client 구현: `adapter/out/...`
- 다른 bounded context를 직접 repository/entity로 참조하지 않는다. 필요한 경우 application port + adapter client 패턴을 따른다.
- 여러 구현체가 있는 adapter bean name은 기존 명시 이름을 유지한다.
  - 예: `postMemberClientAdapter`, `commentMemberClientAdapter`, `guestbookMemberClientAdapter`, `authMemberClientAdapter`
- DB schema는 Flyway migration(`app/src/main/resources/db/migration`) 기준이다.
  - entity 변경 시 migration 추가/수정 여부를 반드시 본다.
  - Hibernate `ddl-auto: validate` 계열 설정이면 entity와 migration 불일치가 바로 장애가 된다.

## 테스트/검증 기준

- domain 값 객체/엔티티 변경: 해당 `domain/src/test` 추가 또는 수정
- use case/service 변경: 해당 `application/src/test` 추가 또는 수정
- controller/mapper/repository adapter 변경: 해당 adapter module 테스트 추가 또는 수정
- DB/entity 변경: mapper + JPA mapping + Flyway migration + `:app:bootJar` 확인
- auth/token 변경: Redis/JWT 설정명과 profile별 `application*.yml` 불일치 여부 확인

최소 검증 기준:

```txt
작은 로직 변경: ./gradlew test
빌드/설정/모듈 변경: ./gradlew clean build
API/DB/env 영향 변경: ./gradlew check && ./gradlew :app:bootJar
```

실행 불가하면 “미실행”으로 넘기지 말고, 왜 못 했는지 로그 핵심을 남긴다.

## 런타임/설정 주의

기본 profile은 `application.yml` 기준 `local`이다. 하지만 local에서도 DB/R2/JWT 등 env가 필요할 수 있다.

중요 env 후보:

```txt
DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD
JWT_SECRET
GITHUB_CLIENT_ID
GITHUB_CLIENT_SECRET
CLOUDFLARE_R2_ACCOUNT_ID
CLOUDFLARE_R2_ACCESS_KEY_ID
CLOUDFLARE_R2_SECRET_ACCESS_KEY
CLOUDFLARE_R2_BUCKET_NAME
CLOUDFLARE_R2_PUBLIC_URL
REDIS_HOST
REDIS_PORT
REDIS_PASSWORD
APP_COOKIE_SECURE
APP_COOKIE_DOMAIN
APP_COOKIE_SAME_SITE
APP_REDIRECT_BASE_URL
APP_REDIRECT_SUCCESS_END_POINT
APP_REDIRECT_ERROR_END_POINT
```

Swagger/OpenAPI 경로 후보:

```txt
/swagger-ui.html
/api-docs
```

## 리스크/금지사항

- 오래된 문서(`CLAUDE.md`, 예전 README 등)만 믿고 구조를 바꾸지 않는다.
- broad formatting, 대규모 package 이동, module 구조 개편은 명시 지시 없이는 하지 않는다.
- Secret 값은 코드/문서/gitops에 직접 쓰지 않는다.
- `kubectl apply`, 원격 배포, production DB 변경은 명시 지시 없이는 하지 않는다.
- CI가 `app/Dockerfile`을 참조할 수 있으니 Docker 관련 작업 전 실제 파일 존재 여부를 확인한다.
- docker-compose가 없을 수 있으므로 local DB/Redis는 별도 준비가 필요하다고 가정한다.
- profile별 `jwt.*` 설정명이 다를 수 있으니 auth 설정 변경 전 실제 properties binding 코드를 확인한다.

## 보고 형식

Discord에서는 표 대신 아래처럼 보고한다.

```txt
변경 repo: server
변경 요약:
- ...

검증:
- ./gradlew test: PASS/FAIL/미실행(사유)
- ./gradlew :app:bootJar: PASS/FAIL/미실행(사유)

영향:
- client: 있음/없음
- gitops: 있음/없음

리스크:
- ...
```
