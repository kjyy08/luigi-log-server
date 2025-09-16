# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Luigi Log Server is a personal tech blog platform being built with Spring Boot 3.5.5 + Kotlin 1.9.25, designed to evolve from a modular monolith to microservices architecture. The platform will feature AI-powered chatbot functionality with RAG (Retrieval Augmented Generation) for enhanced user interaction.

**Current Status**: Package structure implementation complete - hexagonal architecture with modular monolith structure fully created and ready for domain implementation.

## Build and Development Commands

### Essential Commands
```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Run tests
./gradlew test

# Build Docker image
./gradlew bootBuildImage

# Assemble the project
./gradlew assemble

# Run all checks
./gradlew check
```

### Development Environment
```bash
# Run main application (from monolith-main module)
./gradlew :mains:monolith-main:bootRun

# Build specific modules
./gradlew :libs:common-domain:build
./gradlew :service:user:core:build

# Build all modules
./gradlew build -x test
```

*Note: Docker, database, and advanced tooling configurations are not yet implemented.*

## Architecture Overview

### Core Design Principles
- **Hexagonal Architecture**: Clear separation between domain logic and infrastructure using ports and adapters
- **Modular Monolith**: Service boundaries prepared for eventual microservices extraction
- **Event-Driven Architecture**: Domain and integration events via Apache Kafka
- **CQRS Pattern**: PostgreSQL for writes, Elasticsearch for read-optimized queries

### Implemented Project Structure
```
luigi-log-server/
├── mains/
│   └── monolith-main/           # Spring Boot main application
│       ├── src/main/kotlin/cloud/luigi99/blog/
│       │   ├── BlogApplication.kt
│       │   ├── config/
│       │   │   ├── SecurityConfig.kt
│       │   │   ├── JpaConfig.kt
│       │   │   └── WebConfig.kt
│       │   └── presentation/
│       │       └── GlobalExceptionHandler.kt
│       ├── src/main/resources/application.yml
│       └── build.gradle.kts
├── libs/                        # Common libraries (implemented)
│   ├── common-domain/           # Domain layer abstractions
│   │   └── src/main/kotlin/cloud/luigi99/blog/common/
│   │       ├── domain/          # BaseEntity, DomainEvent, AggregateRoot
│   │       └── exception/       # BusinessException, DomainException  
│   ├── common-infrastructure/   # Infrastructure layer commons
│   │   └── src/main/kotlin/cloud/luigi99/blog/common/
│   │       ├── persistence/     # JpaBaseRepository, BaseJpaEntity
│   │       ├── messaging/       # EventPublisher, DomainEventPublisher
│   │       └── security/        # PasswordEncoder, SecurityUtils
│   └── common-web/             # Web layer commons
│       └── src/main/kotlin/cloud/luigi99/blog/common/
│           ├── web/            # ApiResponse, PageResponse, ErrorResponse
│           └── security/       # JwtTokenProvider, SecurityContext
├── service/                    # Domain services (structure implemented)
│   ├── user/                  # User management domain
│   │   ├── core/              # Domain logic, ports, use cases
│   │   ├── adapter-in/        # REST controllers, event handlers  
│   │   └── adapter-out/       # JPA entities, repositories
│   ├── content/               # Content management domain
│   │   ├── core/
│   │   ├── adapter-in/
│   │   └── adapter-out/
│   ├── search/                # Search functionality
│   │   ├── core/
│   │   ├── adapter-in/
│   │   └── adapter-out/
│   ├── analytics/             # Analytics and metrics
│   │   ├── core/
│   │   ├── adapter-in/
│   │   └── adapter-out/
│   └── ai/                    # AI services (Phase 2)
│       ├── core/
│       ├── adapter-in/
│       └── adapter-out/
├── docs/                      # Comprehensive planning documentation
├── build.gradle.kts          # Root build configuration (multi-module)
├── settings.gradle.kts       # All modules registered
└── README.md                 # Project overview in Korean
```

### Hexagonal Architecture Implementation
- **Core (Domain)**: Pure business logic, domain models, ports, and use cases
- **Adapter-In**: REST controllers, event handlers, and external interfaces
- **Adapter-Out**: JPA repositories, messaging, and external system integrations
- **Common Libraries**: Shared domain abstractions, infrastructure utilities, and web concerns

Each service follows the dependency inversion principle: `adapter-in` → `core` ← `adapter-out`

## Implementation Status

### ✅ Completed Components

#### Architecture Foundation
- Multi-module Gradle setup with all 19 modules configured
- Hexagonal architecture package structure implemented
- Dependency management and module relationships established

#### Common Libraries (`libs/`)
- **common-domain**: BaseEntity, DomainEvent, AggregateRoot, ValueObject, business exceptions
- **common-infrastructure**: JPA base repository, domain event publisher, security utilities
- **common-web**: API response models, JWT token provider, validation utilities, security context

#### Main Application (`mains/monolith-main`)
- Spring Boot 3.5.5 application setup
- Security configuration with JWT support
- JPA configuration with auditing
- CORS configuration for API access
- Global exception handling
- Application properties with H2 database for development

#### Service Module Structure (`service/`)
- **user**: User management with authentication (structure ready)
- **content**: Blog post and content management (structure ready)
- **search**: Search functionality (structure ready)
- **analytics**: Usage analytics and metrics (structure ready)
- **ai**: AI chatbot services (basic structure for Phase 2)

### 🔄 Ready for Implementation

#### Domain Models
- User aggregate with profile and session management
- Content aggregates (Post, Category, Tag, Media)
- Search indexing and query models
- Analytics and metrics aggregates

#### Use Cases and Ports
- User registration and authentication flows
- Content CRUD operations with publishing workflow
- Search indexing and query operations
- Analytics data collection and reporting

#### Adapters
- REST API controllers for each domain
- JPA entities and repositories
- Event handlers for domain events
- External service integrations

### Technology Integration Points
- **PostgreSQL**: ACID transactions and complex relational queries
- **Redis**: Session management and application-level caching
- **Elasticsearch**: Full-text search with <100ms response requirement
- **Qdrant**: Vector database for AI embeddings and similarity search
- **Apache Kafka**: Asynchronous event streaming between services
- **OpenAI API**: LLM integration with custom RAG implementation

## Development Evolution Plan

### Phase 1: Foundation (Current)
✅ **Completed**: Hexagonal architecture package structure with modular monolith design  
🔄 **In Progress**: Domain model implementation, JWT authentication, basic blog CRUD operations, and simple search functionality

### Phase 2: AI Integration
Implement hexagonal architecture, basic AI chatbot with RAG, and event-driven communication patterns.

### Phase 3: Infrastructure Modernization
Deploy to K3s cluster with LGTM stack (Loki, Grafana, Tempo, Prometheus) monitoring.

### Phase 4: Microservices Evolution
Advanced Elasticsearch features, sophisticated AI with MCP protocol, and full GitOps automation.

## Performance Requirements

- **Response Time**: Average <200ms, P95 <500ms
- **Availability**: 99.9% uptime target
- **Search Performance**: <100ms for Elasticsearch queries
- **AI Response**: <3 seconds for chatbot interactions
- **Test Coverage**: Minimum 80% required

## AI Features Architecture

The platform includes specialized AI functionality:
- **Blog Content Analyzer**: RAG-based Q&A on blog content
- **Learning Path Advisor**: Technical concept guidance
- **Tech Concept Explainer**: Complex terminology interpretation
- **Related Post Finder**: Content recommendation system

AI services use MCP (Model Context Protocol) for modular server communication and Qdrant for vector similarity search.

## Data Flow Pattern

```
User Request → API Gateway → Service Layer → Domain Logic → 
Event Publication → Kafka → Event Handlers → Data Synchronization
```

## Security and Compliance

- **JWT Authentication**: 24-hour tokens with 30-day refresh
- **Data Encryption**: AES-256 for sensitive data
- **GDPR Compliance**: Privacy by design with automatic PII masking
- **Regular Security Scanning**: Vulnerability assessments and dependency checks

## Infrastructure Targets

- **Container Orchestration**: K3s (lightweight Kubernetes)
- **Observability**: Comprehensive monitoring with LGTM stack
- **Deployment**: GitOps with ArgoCD
- **Environment**: Cloud-native with cost-efficient K3s clusters

## GitHub Guidelines

This section defines the standardized GitHub practices for the Luigi Log Server project, ensuring consistent development processes and high-quality code delivery.

### Issue Template

Use this Issue Template: `.github/ISSUE_TEMPLATE/issue.yml`

### Branch Naming Conventions

Follow the structured branch naming pattern: `type/brief-description-in-kebab-case`

#### Branch Types
- **`feature/`**: New feature development
  - `feature/user-authentication-jwt`
  - `feature/blog-post-crud-operations`
  - `feature/elasticsearch-integration`

- **`bugfix/`**: Bug fixes for existing functionality
  - `bugfix/jwt-token-validation-error`
  - `bugfix/pagination-offset-calculation`

- **`hotfix/`**: Critical production fixes requiring immediate deployment
  - `hotfix/security-vulnerability-cors`
  - `hotfix/database-connection-leak`

- **`docs/`**: Documentation updates and improvements
  - `docs/architecture-documentation-update`
  - `docs/api-endpoint-specifications`

- **`refactor/`**: Code refactoring without functional changes
  - `refactor/hexagonal-architecture-cleanup`
  - `refactor/service-layer-optimization`

- **`chore/`**: Build system, dependency updates, and maintenance tasks
  - `chore/gradle-dependency-updates`
  - `chore/github-actions-workflow-setup`

#### Branch Lifecycle
- Create feature branches from `main`
- Keep branches focused on single features or fixes
- Delete branches after successful merge to maintain repository cleanliness
- Use descriptive names that clearly indicate the work being done

### Commit Message Conventions

Follow Conventional Commits specification with Korean descriptions for better team communication:

#### Format
```
<type>: <description> [- <additional details>] [#issue-number]
```

#### Commit Types
- **`feat`**: New features or enhancements
- **`fix`**: Bug fixes and error corrections
- **`docs`**: Documentation changes
- **`style`**: Code formatting and style changes (no functional impact)
- **`refactor`**: Code refactoring and optimization
- **`test`**: Adding or modifying tests
- **`chore`**: Build system, dependencies, configuration changes
- **`perf`**: Performance improvements
- **`ci`**: Continuous integration changes
- **`build`**: Build system or external dependency changes

#### Examples
```bash
feat: JWT 기반 사용자 인증 시스템 구현 #12
fix: Elasticsearch 연결 타임아웃 오류 수정 - 커넥션 풀 설정 개선 #24
docs: 헥사고날 아키텍처 가이드 문서 추가 #18
refactor: 사용자 도메인 서비스 레이어 리팩토링 - 의존성 역전 원칙 적용 #31
test: 블로그 포스트 CRUD 유닛 테스트 추가 #22
chore: Spring Boot 3.5.5로 업그레이드 #19
```

#### Commit Best Practices
- Write commits in Korean for better team understanding
- Use imperative mood in descriptions
- Include issue numbers when applicable
- Keep the first line under 80 characters
- Provide additional context in the body when necessary

### PR Template

Use this PR Template: `.github/pull_request_template.md`

### Agent Utilization Guidelines

Choose the appropriate agent based on the development task:
- **Domain Implementation**: Use `backend-core-engineer` for business logic
- **API Development**: Use `api-developer` for REST endpoints
- **Data Layer**: Use `database-schema-engineer` for entities and repositories
- **Testing**: Use `testing-engineer` for comprehensive test coverage
- **Project Management**: Use `github-project-manager` for workflow coordination

## Documentation Structure

The project documentation is organized into categorized folders for better maintainability:

### Planning Documents (`docs/planning/`)
- **프로젝트-구현-기획서.md**: Overall project vision, goals, and core feature definitions
- **구현-로드맵.md**: Detailed development timeline and milestones
- **리스크-관리.md**: Risk identification and mitigation strategies

### Architecture Documents (`docs/architecture/`)
- **아키텍처-설계.md**: System architecture principles and structure
- **성능-지표.md**: Performance targets and measurement methods

### AI System Documents (`docs/ai/`)
- **AI-챗봇-상세-기획.md**: Detailed AI chatbot specifications and RAG implementation

### Analysis Documents (`docs/analysis/`)
- **저장소-분석-보고서.md**: Analysis of the target repository that this project is cloning/inspired by