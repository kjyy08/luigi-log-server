# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**blog-server** is a backend REST API for a technical blog and portfolio platform built with:
- **Language:** Kotlin 2.2.21 (JDK 21)
- **Framework:** Spring Boot 3.5.7
- **Architecture:** Domain-Driven Design (DDD) + Hexagonal Architecture
- **Build:** Gradle 8.x multi-module project with Kotlin DSL

## Key Commands

### Build & Run
```bash
./gradlew clean build                  # Full build with tests
./gradlew bootRun                      # Run application (defaults to :app module)
./gradlew :app:bootRun                 # Explicitly run app module
./gradlew :domain:member:build         # Build specific module
```

### Testing
```bash
./gradlew test                         # Run all tests
./gradlew :domain:content:test         # Test specific module
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
│       ├── spring-boot-conventions.gradle.kts  # Spring Boot setup
│       └── kover.gradle.kts                    # Test coverage (60% min)
├── gradle/
│   └── libs.versions.toml # Version catalog (single source of truth)
├── libs/                  # Shared libraries
│   ├── common/            # Shared kernel (DDD base classes, error handling)
│   └── jpa/               # JPA extensions (JpaAggregateRoot with auditing)
├── domain/                # Business logic modules
│   ├── member/            # Authentication (OAuth2 + JWT)
│   ├── content/           # Blog posts, comments, tags
│   └── media/             # File upload and storage
└── app/                   # Application runner (executable JAR)
```

### Hexagonal Architecture Pattern

**Every domain module follows this structure strictly:**

```
domain/{module}/
├── adapter/
│   ├── in/web/              # REST controllers, DTOs
│   └── out/persistence/     # JPA entities, repository implementations
├── application/
│   ├── port/
│   │   ├── in/              # Use case interfaces (commands)
│   │   └── out/             # Repository interfaces (ports)
│   └── service/             # Service implementations
└── domain/                  # Pure domain model (NO Spring dependencies)
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

The `libs/jpa` module provides JPA-specific extensions:

- **`JpaAggregateRoot<T>`** - Extends `AggregateRoot` with JPA persistence
  - Adds `@Id`, `createdAt`, `updatedAt` fields
  - Implements `Persistable<T>` for proper JPA identity handling
  - Enables JPA Auditing via `@EntityListeners(AuditingEntityListener::class)`

**Example usage:**
```kotlin
// Pure domain model (domain/member/domain)
class Member(id: MemberId) : AggregateRoot<MemberId>(id) {
    fun register() {
        registerEvent(MemberRegisteredEvent(this.id))
    }
}

// JPA persistence entity (domain/member/adapter/out/persistence)
@Entity
@Table(name = "member")
class MemberJpaEntity(
    @Id override val id: MemberId,
    val email: String
) : JpaAggregateRoot<MemberId>(id)
```

## Key Configuration Files

### Version Management
- **`gradle/libs.versions.toml`** - All dependency versions (update here!)
- Bundles available: `spring-boot-web`, `spring-boot-security`, `spring-boot-data`, `jwt`

### Build Configuration
- **`buildSrc/`** - Convention plugins defining standards for all modules
  - Apply via: `plugins { id("springBootConventions") }`
- **`settings.gradle.kts`** - Module declarations

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

1. **Choose the correct module**: Member (auth), Content (posts), or Media (files)
2. **Follow hexagonal structure**:
   - Add domain model in `domain/` (pure Kotlin, no annotations)
   - Define port interfaces in `application/port/in` (use cases) and `port/out` (repositories)
   - Implement service in `application/service/`
   - Create REST endpoints in `adapter/in/web/`
   - Implement persistence in `adapter/out/persistence/`
3. **Use base classes**:
   - Domain models extend `AggregateRoot<T>` or `DomainEntity<T>` from `libs/common`
   - JPA entities extend `JpaAggregateRoot<T>` from `libs/jpa`
4. **Error handling**: Throw `BusinessException` with appropriate `ErrorCode`

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

## Security

- **Authentication:** JWT tokens (JJWT 0.13.0)
- **OAuth2:** Social login (Google/GitHub) via Spring Security
- **Session:** Stateless (no server-side sessions)
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
Defined in `libs/common/exception/ErrorCode.kt`:
- Prefix by domain: `M001` (Member), `P001` (Post), `C001` (Comment), `F001` (File/Media)
- Includes HTTP status and message

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
libs/common (pure domain, no framework dependencies)
    ↑
libs/jpa (JPA extensions, depends on libs/common)
    ↑
domain/* (each domain module depends on libs/common and libs/jpa)
    ↑
app (composes all domain modules into executable application)
```

**Key principle:** Domain modules are independent libraries. Only `app` module produces an executable JAR (bootJar enabled).

## Reference Documentation

- **Architecture Decisions:** `docs/tech-blog.md` (Korean)
- **Main Application:** `app/src/main/kotlin/cloud/luigi99/blog/api/BlogServerApplication.kt`
- **Security Config:** `app/src/main/kotlin/cloud/luigi99/blog/api/config/SecurityConfig.kt`
- **Global Exception Handler:** `app/src/main/kotlin/cloud/luigi99/blog/api/config/GlobalExceptionHandler.kt`
