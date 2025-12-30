# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**blog-server** is a backend REST API for a technical blog and portfolio platform built with:
- **Language:** Kotlin 2.2.21 (JDK 21)
- **Framework:** Spring Boot 4.0.0 / Spring Framework 7.0.0
- **Architecture:** Domain-Driven Design (DDD) + Hexagonal Architecture
- **Build:** Gradle 8.x multi-module project with Kotlin DSL

## Key Commands

### Build & Run
```bash
./gradlew clean build                  # Full build with tests
./gradlew bootRun                      # Run application (defaults to :app module)
./gradlew :app:bootRun                 # Explicitly run app module
./gradlew :modules:member:domain:build # Build specific module
```

### Testing
```bash
./gradlew test                         # Run all tests
./gradlew :modules:content:test        # Test specific module
./gradlew test --tests ClassName       # Run specific test class
./gradlew test --debug-jvm             # Debug tests on port 5005
```

### Code Quality
```bash
./gradlew ktlintCheck                  # Check code style
./gradlew ktlintFormat                 # Auto-fix code style
./gradlew koverHtmlReport              # Generate coverage report
./gradlew koverVerify                  # Verify 60% minimum coverage
./gradlew check                        # Run all checks (tests + lint + coverage)
```

## Architecture

### Multi-Module Structure

```
blog-server/
├── buildSrc/              # Convention plugins (build logic)
│   └── src/main/kotlin/plugin/
│       ├── conventions.gradle.kts              # Base: JDK 21, Kotlin, Ktlint
│       ├── spring-library-conventions.gradle.kts  # Spring Library setup
│       ├── spring-boot-conventions.gradle.kts  # Spring Boot setup
│       └── kover.gradle.kts                    # Test coverage (60% min)
├── gradle/
│   └── libs.versions.toml # Version catalog (single source of truth)
├── libs/                  # Shared libraries
│   ├── common/            # Shared kernel (DDD base classes, error handling)
│   └── adapter/           # Framework adapters
│       ├── persistence/jpa/    # JPA extensions (JpaAggregateRoot with auditing)
│       ├── persistence/redis/  # Redis configuration and extensions
│       ├── message/spring/     # Domain event publishing via Spring
│       └── web/                # REST common utilities (GlobalExceptionHandler, etc.)
├── modules/               # Domain modules (bounded contexts)
│   ├── member/            # Member profiles and management
│   ├── auth/
│   │   ├── token/         # JWT token management (Redis-backed)
│   │   └── credentials/   # OAuth2 authentication credentials
│   ├── content/           # Blog posts, comments, tags
│   └── media/             # File upload and storage
└── app/                   # Application runner (executable JAR)
```

### Hexagonal Architecture Pattern

**Every domain module follows this structure strictly:**

```
modules/{module}/
├── domain/                  # Pure domain model (NO Spring dependencies)
│   ├── model/               # Aggregate roots and entities
│   ├── vo/                  # Value objects (immutable, @JvmInline recommended)
│   ├── event/               # Domain events
│   └── exception/           # Domain-specific exceptions
├── application/
│   ├── port/
│   │   ├── in/              # Use case interfaces (commands/queries)
│   │   └── out/             # Repository port interfaces
│   └── service/             # Service implementations (business logic)
└── adapter/
    ├── in/
    │   ├── web/             # REST controllers, DTOs, Swagger annotations
    │   └── event/           # Event listeners (optional)
    └── out/
        ├── persistence/jpa/ # JPA entities, repository adapters, mappers
        └── client/          # External API clients (optional)
```

**Dependency flow:** Controller → UseCase (port/in) → Service → Repository (port/out) → Domain

**Critical rule:** Domain layer has ZERO framework dependencies. All Spring annotations live in adapters/application only.

### DDD Building Blocks

The `libs/common` module provides base classes used across all domains:

- **`DomainEntity<T>`** - Base for entities with identity
- **`AggregateRoot<T>`** - Transaction boundaries + domain event publishing
  - Methods: `registerEvent()`, `clearEvents()`, `getEvents()`
- **`ValueObject`** - Marker interface for immutable value objects
- **`DomainEvent`** - Interface for domain events
- **`BusinessException`** - Base exception with `ErrorCode` enum
- **`ApiResponse<T>`** - Standardized REST API response wrapper

The `libs/adapter` modules provide framework-specific implementations:

**libs/adapter/persistence/jpa:**
- **`JpaAggregateRoot<ID : ValueObject>`** - Extends `AggregateRoot` with JPA persistence
  - Implements `Persistable<ID>` for proper JPA identity handling
  - Enables JPA Auditing via `@EntityListeners(AuditingEntityListener::class)`
  - Adds `createdAt`, `updatedAt` fields (marked `@CreatedDate`, `@LastModifiedDate`)
- **`JpaDomainEntity`** - Base for non-root JPA entities
- **`JpaConfig`** - Enables JPA auditing, repositories, and entity scanning

**libs/adapter/persistence/redis:**
- **`RedisConfig`** - Configures `RedisTemplate<String, Any>` with JSON serialization
- **`RedisDomainEntity`** - Base for Redis-persisted entities
- Enables `@EnableRedisRepositories`

**libs/adapter/message/spring:**
- **`EventConfiguration`** - Initializes `EventManager` with Spring implementation
- **`SpringDomainEventPublisher`** - Publishes domain events via `ApplicationEventPublisher`
- **`SpringEventContextManager`** - Manages event context per transaction

**libs/adapter/web:**
- **`CommonResponse<T>`** - Standardized API response wrapper
- **`GlobalExceptionHandler`** - Centralized exception handling (maps `BusinessException` → HTTP status)
- **`CookieUtils`** - Cookie manipulation utilities
- **`SwaggerConfig`** - SpringDoc OpenAPI configuration

**Example usage:**
```kotlin
// Pure domain model (modules/member/domain/model)
class Member private constructor(
    override val entityId: MemberId,
    val email: Email,
    val username: Username
) : AggregateRoot<MemberId>() {
    fun register(): Member {
        registerEvent(MemberRegisteredEvent(entityId, email))
        return this
    }
}

// JPA persistence entity (modules/member/adapter/out/persistence/jpa)
@Entity
@Table(name = "member")
class MemberJpaEntity : JpaAggregateRoot<MemberId>() {
    @Id override val id: UUID
    @Column(nullable = false, unique = true)
    lateinit var email: String

    @Column(nullable = false)
    lateinit var username: String
}

// Mapper (modules/member/adapter/out/persistence/jpa)
object MemberMapper {
    fun toDomain(entity: MemberJpaEntity): Member =
        Member.from(
            entityId = MemberId(entity.id),
            email = Email(entity.email),
            username = Username(entity.username),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )

    fun toEntity(member: Member): MemberJpaEntity =
        MemberJpaEntity().apply {
            id = member.entityId.value
            email = member.email.value
            username = member.username.value
        }
}
```

## Key Configuration Files

### Version Management
- **`gradle/libs.versions.toml`** - All dependency versions (update here!)
- Bundles available: `spring-boot-web`, `spring-boot-security`, `spring-boot-data`, `jwt`, `redis`, `spring-ai`, `kotlin-test`

### Build Configuration
- **`buildSrc/`** - Convention plugins defining standards for all modules
  - `conventions` - Base plugin (JDK 21, Kotlin, Ktlint) for `libs/common`
  - `spring-library-conventions` - Spring Library plugin for `libs/adapter/*`
  - `spring-boot-conventions` - Spring Boot plugin for `modules/*` (disables bootJar)
  - Apply via: `plugins { id("springBootConventions") }`
- **`settings.gradle.kts`** - Module declarations (30+ submodules)

### Application Configuration
- **`app/src/main/resources/application.yml`** - Base configuration
- **`application-{local,dev,prod}.yml`** - Profile-specific configs
- JPA: `open-in-view: false`, `ddl-auto: validate` (use Flyway migrations)

### Database
- **Tool:** Flyway 11.13.2
- **Migrations:** `app/src/main/resources/db/migration/V1__init_schema.sql`
- **Database:** PostgreSQL 16

## Development Guidelines

### Adding New Features

1. **Choose the correct module**: Member, Auth (token/credentials), Content, or Media
2. **Follow hexagonal structure** (in this order):
   - Add domain model in `domain/model/` (pure Kotlin, extends `AggregateRoot<T>` or `DomainEntity<T>`)
   - Create value objects in `domain/vo/` (`@JvmInline value class` recommended for IDs)
   - Define domain events in `domain/event/` (implement `DomainEvent` interface)
   - Create domain exceptions in `domain/exception/` (extend `BusinessException`)
   - Define port interfaces in `application/port/in/` (use cases) and `port/out/` (repositories)
   - Implement services in `application/service/` (business logic)
   - Create REST endpoints in `adapter/in/web/` (controllers, DTOs, Swagger)
   - Implement persistence in `adapter/out/persistence/jpa/` (JPA entities, repository adapters, mappers)
3. **Use base classes**:
   - Domain models extend `AggregateRoot<T>` or `DomainEntity<T>` from `libs/common`
   - JPA entities extend `JpaAggregateRoot<ID>` from `libs/adapter/persistence/jpa`
   - Redis entities extend `RedisDomainEntity` from `libs/adapter/persistence/redis`
4. **Mapper pattern**: Always create separate JPA entities and domain models, use mapper objects to convert
5. **Error handling**: Throw `BusinessException` with appropriate `ErrorCode` defined in domain exceptions

### Code Style

- **Ktlint enforced**: Run `./gradlew ktlintFormat` before committing
- **Style:** Official Kotlin code style (version 1.0.1)
- **Coverage:** Minimum 60% (excludes: `*Application*`, `*.config.*`, `*.dto.*`, `*.entity.*`)

### Testing

- **Framework:** Kotest 6.0.7 with JUnit 5
- **Mocking:** MockK 1.14.5
- Tests must maintain 60% coverage (`./gradlew koverVerify`)

### Cross-Module Communication

- **No direct imports between domain modules**
- Future: Use domain events or API composition
- Each domain is a bounded context (DDD)

## API Documentation

- **Tool:** SpringDoc OpenAPI 2.8.14
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI Spec:** `http://localhost:8080/api-docs`

## Security & Authentication

The authentication system is split into two separate bounded contexts:

### Auth Module Architecture

**modules/auth/token/** (JWT token management, Redis-backed):
- **Domain:** `AuthToken` (refresh tokens), `TokenId` value object
- **Application:** `IssueTokenUseCase`, `ReissueTokenUseCase`, `RevokeTokenUseCase`
- **Adapter/Out:** JWT token provider using JJWT 0.13.0, Redis repository for refresh tokens
- **Purpose:** Token lifecycle management (issue, reissue, revoke, validate)

**modules/auth/credentials/** (OAuth2 authentication, JPA-backed):
- **Domain:** `MemberCredentials` (OAuth provider, roles), `OAuthInfo` value object
- **Application:** Authentication flow, credential management
- **Adapter/In:** OAuth2 login handlers, security filters, JWT authentication filter
- **Adapter/Out:** JPA repository for credentials
- **Purpose:** OAuth2 social login (Google/GitHub) and credential storage

**Key points:**
- **Stateless:** No server-side sessions, JWT tokens only
- **Separation:** Token management (Redis) separated from credential management (JPA)
- **OAuth2:** Spring Security OAuth2 Client for social login
- **CORS:** Configured for localhost:3000 and localhost:5173

## Common Patterns

### Standardized API Responses
All endpoints return:
```json
{
  "success": true,
  "data": { ... },
  "error": null,
  "timestamp": "2025-12-03T..."
}
```

Errors:
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "P001",
    "message": "Post not found"
  },
  "timestamp": "2025-12-03T..."
}
```

### Error Codes
Defined in domain-specific exception classes:
- `COMMON_001` - Invalid input (400)
- `COMMON_002` - Internal server error (500)
- `AUTH_001` - Unauthorized (401)
- `AUTH_002` - Invalid token (401)
- `AUTH_003` - Access denied (403)
- `MEMBER_001` - Member not found (404)
- `CREDENTIAL_001` - Credential not found (404)
- `PROFILE_001` - Profile not found (404)
- `CONTENT_001` - Post not found (404)
- `MEDIA_001` - File upload failed (500)

### Domain Events
Event-driven architecture using service locator pattern:

```kotlin
// Domain model publishes events
class Member : AggregateRoot<MemberId>() {
    fun register(): Member {
        registerEvent(MemberRegisteredEvent(entityId, email))
        return this
    }
}

// Event is managed by EventManager (service locator)
object EventManager {
    fun add(event: DomainEvent)
    fun clear()
    fun toListAndClear(): List<DomainEvent>
}

// Spring adapter publishes events to ApplicationEventPublisher
@Component
class SpringDomainEventPublisher(
    private val publisher: ApplicationEventPublisher
) : DomainEventPublisher {
    override fun publish(event: DomainEvent) {
        publisher.publishEvent(event)
    }
}

// Event listeners in adapter/in/event
@Component
class MemberEventListener {
    @EventListener
    fun handle(event: MemberRegisteredEvent) {
        // Side effects (send email, create profile, etc.)
    }
}
```

### Database Migrations
- Use Flyway for all schema changes
- Location: `app/src/main/resources/db/migration/`
- Naming: `V{version}__{description}.sql`
- Single table names (not plural): `member`, `post`, `tag`

## Important Architecture Decisions

1. **Stateless authentication** - JWT tokens, no session state
2. **Markdown storage** - Blog content stored as raw markdown
3. **Storage abstraction** - Media files abstracted for S3/local filesystem
4. **Event-driven domain** - AggregateRoot publishes events for side effects
5. **Centralized error handling** - GlobalExceptionHandler maps domain exceptions to HTTP
6. **Convention over configuration** - buildSrc plugins eliminate boilerplate
7. **No cross-module coupling** - Domains evolve independently

## Module Dependencies

**Dependency hierarchy (bottom to top):**
```
libs/common (pure domain, NO framework dependencies)
    ↑
libs/adapter/* (framework adapters: jpa, redis, message/spring, web)
    ↑
modules/*/domain (pure domain models, depends only on libs/common)
    ↑
modules/*/application (business logic, depends on domain + libs/common)
    ↑
modules/*/adapter (infrastructure, depends on application + libs/adapter/*)
    ↑
app (composes all modules, only module with bootJar enabled)
```

**Key principles:**
- `libs/common` has ZERO Spring dependencies (pure Kotlin + kotlin-logging only)
- `modules/*/domain` has ZERO Spring dependencies (pure domain models)
- Only `adapter/*` and `application/*` layers use Spring annotations
- Domain modules are independent libraries (bootJar disabled)
- Only `app` module produces an executable JAR (bootJar enabled)
- Auth module is split: `auth/token` (JWT, Redis) and `auth/credentials` (OAuth2, JPA)

## Reference Documentation

- **Main Application:** `app/src/main/kotlin/cloud/luigi99/blog/app/BlogServerApplication.kt`
- **Component Scan:** Scans `cloud.luigi99.blog.app`, `cloud.luigi99.blog.adapter.*`, `cloud.luigi99.blog.member`, `cloud.luigi99.blog.auth`, `cloud.luigi99.blog.content`, `cloud.luigi99.blog.media`, `cloud.luigi99.blog.common`
- **Global Exception Handler:** `libs/adapter/web/src/main/kotlin/cloud/luigi99/blog/adapter/web/GlobalExceptionHandler.kt`
- **Security Config:** `modules/auth/credentials/adapter/in/web/src/main/kotlin/.../config/SecurityConfig.kt`
- **JPA Config:** `libs/adapter/persistence/jpa/src/main/kotlin/.../JpaConfig.kt`
- **Redis Config:** `libs/adapter/persistence/redis/src/main/kotlin/.../RedisConfig.kt`
