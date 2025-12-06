# GEMINI.md

This file provides context for the Gemini agent when working on the `blog-server` project.

## 1. Project Overview

**blog-server** is a backend REST API for a technical blog and portfolio platform.
It is designed using **Domain-Driven Design (DDD)** and **Hexagonal Architecture** principles to ensure maintainability and scalability.

*   **Primary Language:** Kotlin 2.2.21 (JDK 21)
*   **Framework:** Spring Boot 3.5.7
*   **Build System:** Gradle 8.x (Kotlin DSL)
*   **Database:** PostgreSQL 16 (Managed via Flyway)
*   **Authentication:** JWT (Stateless) + OAuth2 (Social Login)
*   **Documentation:** SpringDoc OpenAPI (Swagger)

## 2. Architecture & Module Structure

The project follows a strict multi-module structure to enforce architectural boundaries.

### Module Hierarchy

```
blog-server/
├── buildSrc/              # Build logic and convention plugins
├── gradle/                # Version catalog (libs.versions.toml)
├── libs/                  # Shared libraries
│   ├── common/            # Shared kernel (ErrorCode, DomainEntity, etc.) - NO Framework Dependencies
│   └── jpa/               # JPA extensions (JpaAggregateRoot) - Depends on :libs:common
├── domain/                # Business logic (Hexagonal Architecture)
│   ├── member/            # Authentication & Member management
│   ├── content/           # Blog posts, comments, tags (CQRS)
│   └── media/             # File upload/storage abstraction
└── app/                   # Application runner (BootJar) - Depends on all domains
```

### Hexagonal Architecture (Per Domain Module)

Each module under `domain/` **MUST** follow this internal structure:

```
domain/{module}/
├── adapter/
│   ├── in/web/              # REST Controllers, DTOs (Input Adapters)
│   └── out/persistence/     # JPA Entities, Repository Impls (Output Adapters)
├── application/
│   ├── port/
│   │   ├── in/              # Use Case Interfaces (Commands/Queries)
│   │   └── out/             # Repository Interfaces (Ports)
│   └── service/             # Business Logic Implementations
└── domain/                  # Pure Domain Model (POJOs/Data Classes) - NO Spring Annotations
```

**Critical Dependency Rules:**
1.  **Domain Layer:** Pure Kotlin. NO Spring/JPA imports.
2.  **Application Layer:** Depends on Domain. Defines Ports.
3.  **Adapter Layer:** Depends on Application. Implements Adapters (Controller, JPA Repos).
4.  **Framework Isolation:** Spring annotations (`@RestController`, `@Service`, `@Repository`, `@Entity`) belong ONLY in `adapter` and `application` layers (Service impls). The `domain` package remains pure.

## 3. Key Development Commands

Use `./gradlew` for all tasks.

| Task | Command | Description |
| :--- | :--- | :--- |
| **Build** | `./gradlew clean build` | Full build including tests |
| **Run App** | `./gradlew bootRun` | Run the main application |
| **Run Module** | `./gradlew :app:bootRun` | Explicitly run the app module |
| **Test All** | `./gradlew test` | Run all unit/integration tests |
| **Test Module** | `./gradlew :domain:content:test` | Test specific domain module |
| **Lint Check** | `./gradlew ktlintCheck` | Check code style compliance |
| **Lint Fix** | `./gradlew ktlintFormat` | Auto-fix code style issues |
| **Coverage** | `./gradlew koverVerify` | Verify min 60% test coverage |

## 4. Coding Conventions

*   **Kotlin:** Follow official Kotlin coding conventions.
*   **Style:** Enforced by Ktlint. Run `ktlintFormat` before ensuring task completion.
*   **Testing:**
    *   Framework: **Kotest** (w/ JUnit 5 runner) & **MockK**.
    *   Structure: Describe/Context/It or BehaviorSpec styles.
    *   Coverage: Aim for >60% line coverage.
*   **Entities:**
    *   Domain Models: Extend `AggregateRoot<ID>` or `DomainEntity<ID>` (from `:libs:common`).
    *   JPA Entities: Extend `JpaAggregateRoot<ID>` (from `:libs:jpa`) in `adapter/out/persistence`.
*   **API Responses:** Use `ApiResponse<T>` wrapper for consistent JSON structure.
*   **Exceptions:** Throw `BusinessException` with specific `ErrorCode`.

## 5. Key Files & Configuration

*   **Versions:** `gradle/libs.versions.toml` (Single source of truth for deps).
*   **Build Logic:** `buildSrc/src/main/kotlin/plugin/*.gradle.kts`.
*   **DB Migrations:** `app/src/main/resources/db/migration/V{version}__{desc}.sql`.
*   **API Docs:** `http://localhost:8080/swagger-ui.html` (when running).
*   **Global Config:** `app/src/main/resources/application.yml`.

## 6. Common Tasks Checklist

When implementing a new feature:
1.  [ ] Define **Domain Model** in `domain/{module}/domain`.
2.  [ ] Define **Use Case Interface** (Port In) in `domain/{module}/application/port/in`.
3.  [ ] Define **Repository Interface** (Port Out) in `domain/{module}/application/port/out`.
4.  [ ] Implement **Service** in `domain/{module}/application/service`.
5.  [ ] Implement **JPA Adapter** in `domain/{module}/adapter/out/persistence`.
6.  [ ] Implement **Web Adapter** (Controller) in `domain/{module}/adapter/in/web`.
7.  [ ] Add **Tests** (Unit for Domain/Service, Integration for Adapters).
8.  [ ] Run `./gradlew ktlintFormat` and `./gradlew test`.
