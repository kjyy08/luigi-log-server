# Module Structure

## Directory Structure

```
modules/{module-name}/
├── domain/
│   └── src/main/kotlin/cloud/luigi99/blog/{module}/domain/
│       └── {subdomain}/
│           ├── model/                    # Aggregate Root, Entity
│           ├── vo/                       # Value Objects
│           ├── event/                    # Domain Events
│           └── exception/                # Domain Exceptions
│
├── application/
│   └── src/main/kotlin/cloud/luigi99/blog/{module}/application/
│       └── {subdomain}/
│           ├── port/
│           │   ├── in/
│           │   │   ├── command/          # Command UseCases
│           │   │   └── query/            # Query UseCases
│           │   └── out/                  # Repository Ports
│           └── service/
│               ├── command/              # Command Services
│               └── query/                # Query Services
│
└── adapter/
    ├── in/
    │   └── web/
    │       └── src/main/kotlin/cloud/luigi99/blog/{module}/adapter/in/web/
    │           └── {subdomain}/
    │               ├── {Subdomain}Api.kt          # Swagger interface
    │               ├── {Subdomain}Controller.kt   # Controller
    │               └── dto/                       # Request/Response DTOs
    │
    └── out/
        └── persistence/jpa/
            └── src/main/kotlin/cloud/luigi99/blog/{module}/adapter/out/persistence/jpa/
                └── {subdomain}/
                    ├── {Entity}JpaEntity.kt
                    ├── {Entity}JpaRepository.kt
                    ├── {Entity}Mapper.kt
                    └── {Entity}RepositoryAdapter.kt
```

## Dependency Flow

```
Domain (pure business logic)
    ↑
Application (UseCase orchestration)
    ↑
Adapter (framework integration)
```

**Rules:**
- Domain: No Spring dependencies, only `libs/common`
- Application: Domain + minimal Spring (`@Service`, `@Transactional`)
- Adapter: Application + full framework (Spring Web, JPA)

## Layer Responsibilities

| Layer | Purpose | Example |
|-------|---------|---------|
| **Domain** | Business rules, invariants | `Member`, `Email`, `MemberRegisteredEvent` |
| **Application** | UseCase coordination, transaction boundary | `RegisterMemberService` |
| **Adapter/In** | HTTP handling, Swagger docs | `MemberController` |
| **Adapter/Out** | Persistence, ORM mapping | `MemberRepositoryAdapter` |
