# 📁 Project Plan: Tech Blog Server (Kotlin/Spring Boot)

## 1\. 프로젝트 개요

* **프로젝트명**: `tech-blog-server`
* **목표**: DDD/Hexagonal 아키텍처 기반의 확장 가능한 개인 기술 블로그 및 포트폴리오 백엔드 구축.
* **핵심 기능**:
    * **Content**: 마크다운 기반 블로그/포트폴리오 관리 (CQRS).
    * **Auth**: 소셜 로그인 (Google/GitHub) 및 JWT 인증 (비밀번호 없음).
    * **AI (RAG)**: 작성된 글을 지식으로 학습하여 답변하는 AI 챗봇 (Spring AI 1.1, Guest UUID 지원).
    * **Media**: 이미지 업로드 및 관리 (Storage 추상화).

## 2\. 기술 스택 및 버전 (Fixed)

**`gradle/libs.versions.toml`** 에 정의될 고정 버전입니다.

* **Language**: Kotlin `2.2.21` (JDK 21)
* **Framework**: Spring Boot `3.5.7`
* **AI Framework**: Spring AI `1.1.0`
* **Build Tool**: Gradle 8.x (Kotlin DSL)
* **Database**: PostgreSQL 16
* **Auth**: Spring Security, JJWT `0.13.0`
* **Docs**: SpringDoc OpenAPI (Swagger) `2.8.14`

## 3\. 프로젝트 구조 (Gradle Multi-Module)

루트 최상단에 빌드 로직(`buildSrc`)과 버전 카탈로그(`gradle`)를 배치하고, 도메인과 실행 모듈을 분리합니다.

```text
blog-server (Root)
├── buildSrc                <-- [Build Logic] 공통 빌드 스크립트 (Convention Plugins)
│   ├── build.gradle.kts
│   └── src/main/kotlin
│       ├── conventions.gradle.kts       <-- [Base] JDK 21, Kotlin JVM, Ktlint
│       ├── kover.gradle.kts     <-- [Coverage] Kover Report
│       └── spring-boot-conventions.gradle.kts <-- [Framework] Boot 기본 설정
├── gradle
│   └── libs.versions.toml  <-- [Version Control] 라이브러리 버전 중앙 관리
├── common                  <-- [Shared Kernel] Error, Response, Aggregate, Domain Event
├── domain                  <-- [Domains] 비즈니스 로직 (헥사고날)
│   ├── member              <-- Social Login, JWT
│   ├── content             <-- Post, Comment (CQRS)
│   ├── media               <-- File Upload
│   └── ai                  <-- RAG, Chat
└── api                     <-- [Runner] 실행 모듈 (BootJar 생성, 통합 설정)
```

## 4\. 아키텍처 가이드라인 (Hexagonal)

모든 `domain` 모듈은 아래 패키지 구조를 엄격히 준수합니다.

```text
blog-server.{module}
├── adapter
│   ├── in  (web: Controller, DTO)
│   └── out (persistence: Entity, RepositoryImpl)
├── application
│   ├── port
│   │   ├── in  (UseCase/Command Interface)
│   │   └── out (Port Interface)
│   └── service (Service Implementation)
└── domain (Pure Domain Model)
```

## 5\. 데이터베이스 설계 (DBML)

**단수형 테이블 이름**, **소셜 로그인 전용**, **업로더 ID 제거**, **Guest UUID** 적용 완료.

```dbml
// 1. Member Context
Table member {
  id uuid [pk, default: `gen_random_uuid()`]
  email varchar [unique, not null]
  username varchar [not null]
  role varchar [not null] // ROLE_ADMIN, ROLE_GUEST
  provider varchar [not null] // google, github
  provider_id varchar [not null]
  created_at timestamp
}

// 2. Content Context
Enum content_type { BLOG, PORTFOLIO }
Enum post_status { DRAFT, PUBLISHED, ARCHIVED }

Table post {
  id uuid [pk, default: `gen_random_uuid()`]
  title varchar [not null]
  slug varchar [unique, not null]
  body text [not null] // Pure Markdown
  type content_type [not null]
  status post_status [not null, default: 'DRAFT']
  created_at timestamp
  updated_at timestamp
}

Table tag {
  id bigint [pk, increment]
  name varchar [unique, not null]
}

Table post_tag {
  post_id uuid [ref: > post.id]
  tag_id bigint [ref: > tag.id]
  indexes { (post_id, tag_id) [pk] }
}

Table comment {
  id uuid [pk, default: `gen_random_uuid()`]
  post_id uuid [not null]
  parent_id uuid [ref: > comment.id]
  content text [not null]
  writer_name varchar [not null]
  writer_password varchar [null]
  member_id uuid [ref: > member.id]
  is_deleted boolean [default: false]
  created_at timestamp
}
Ref: comment.post_id > post.id [delete: cascade]

// 3. Media Context
Table media_file {
  id uuid [pk, default: `gen_random_uuid()`]
  original_name varchar [not null]
  mime_type varchar [not null]
  size_bytes bigint [not null]
  storage_key varchar [not null]
  public_url varchar [not null]
  created_at timestamp
}

// 4. AI Chat Context
Table vector_store {
  id uuid [pk, default: `gen_random_uuid()`]
  content text
  metadata jsonb // RAG Filtering tags
  embedding vector(1536) // pgvector
}

Table chat_session {
  id uuid [pk, default: `gen_random_uuid()`]
  guest_session_id varchar [not null] // Frontend Generated UUID
  title varchar
  created_at timestamp
  updated_at timestamp
}

Enum message_role { USER, ASSISTANT, SYSTEM, TOOL }

Table chat_message {
  id uuid [pk, default: `gen_random_uuid()`]
  session_id uuid [not null]
  role message_role [not null]
  content text [not null]
  created_at timestamp
  updated_at timestamp
  indexes { (session_id, created_at) }
}
Ref: chat_message.session_id > chat_session.id [delete: cascade]
```
