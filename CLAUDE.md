# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Luigi Log Server is a personal tech blog platform built with Spring Boot 3.5.5 + Kotlin 1.9.25, designed to evolve from a modular monolith to microservices architecture. The platform features AI-powered chatbot functionality with RAG (Retrieval Augmented Generation) for enhanced user interaction.

## Build and Development Commands

### Essential Commands
```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Run tests with coverage
./gradlew test jacocoTestReport

# Static analysis
./gradlew detekt

# Build Docker image
./gradlew bootBuildImage

# Database migrations
./gradlew flywayMigrate

# Generate API documentation
./gradlew generateOpenApiDocs
```

### Development Environment
```bash
# Start local development stack (PostgreSQL, Redis, Elasticsearch, Kafka)
docker-compose up -d

# Dependency vulnerability check
./gradlew dependencyCheckAnalyze
```

## Architecture Overview

### Core Design Principles
- **Hexagonal Architecture**: Clear separation between domain logic and infrastructure using ports and adapters
- **Modular Monolith**: Service boundaries prepared for eventual microservices extraction
- **Event-Driven Architecture**: Domain and integration events via Apache Kafka
- **CQRS Pattern**: PostgreSQL for writes, Elasticsearch for read-optimized queries

### Complete Package Structure
```
luigi-log-server/
├── mains/
│   └── monolith-main/
│       ├── src/main/kotlin/
│       │   └── cloud/luigi99/blog/
│       │       ├── BlogApplication.kt
│       │       ├── config/
│       │       │   ├── SecurityConfig.kt
│       │       │   ├── JpaConfig.kt
│       │       │   ├── WebConfig.kt
│       │       │   ├── KafkaConfig.kt
│       │       │   └── ElasticsearchConfig.kt
│       │       └── presentation/
│       │           └── GlobalExceptionHandler.kt
│       └── build.gradle.kts
├── libs/
│   ├── common-domain/
│   │   ├── src/main/kotlin/
│   │   │   └── cloud/luigi99/blog/common/
│   │   │       ├── domain/
│   │   │       │   ├── BaseEntity.kt
│   │   │       │   ├── DomainEvent.kt
│   │   │       │   ├── AggregateRoot.kt
│   │   │       │   └── ValueObject.kt
│   │   │       ├── exception/
│   │   │       │   ├── BusinessException.kt
│   │   │       │   └── DomainException.kt
│   │   │       └── util/
│   │   │           ├── DateUtils.kt
│   │   │           └── StringUtils.kt
│   │   └── build.gradle.kts
│   ├── common-infrastructure/
│   │   ├── src/main/kotlin/
│   │   │   └── cloud/luigi99/blog/common/
│   │   │       ├── persistence/
│   │   │       │   ├── JpaBaseRepository.kt
│   │   │       │   ├── EventStore.kt
│   │   │       │   └── BaseJpaEntity.kt
│   │   │       ├── messaging/
│   │   │       │   ├── EventPublisher.kt
│   │   │       │   └── DomainEventPublisher.kt
│   │   │       └── security/
│   │   │           ├── PasswordEncoder.kt
│   │   │           └── SecurityUtils.kt
│   │   └── build.gradle.kts
│   └── common-web/
│       ├── src/main/kotlin/
│       │   └── cloud/luigi99/blog/common/
│       │       ├── web/
│       │       │   ├── ApiResponse.kt
│       │       │   ├── PageResponse.kt
│       │       │   ├── ErrorResponse.kt
│       │       │   └── ValidationUtils.kt
│       │       └── security/
│       │           ├── JwtTokenProvider.kt
│       │           └── SecurityContext.kt
│       └── build.gradle.kts
├── service/
│   ├── user/
│   │   ├── core/
│   │   │   ├── src/main/kotlin/
│   │   │   │   └── cloud/luigi99/blog/user/
│   │   │   │       ├── domain/
│   │   │   │       │   ├── model/
│   │   │   │       │   │   ├── User.kt
│   │   │   │       │   │   ├── Profile.kt
│   │   │   │       │   │   ├── UserSession.kt
│   │   │   │       │   │   └── Email.kt
│   │   │   │       │   ├── service/
│   │   │   │       │   │   ├── UserService.kt
│   │   │   │       │   │   └── AuthenticationService.kt
│   │   │   │       │   └── repository/
│   │   │   │       │       └── UserRepository.kt
│   │   │   │       ├── application/
│   │   │   │       │   ├── usecase/
│   │   │   │       │   │   ├── UserManagementUseCase.kt
│   │   │   │       │   │   └── AuthenticationUseCase.kt
│   │   │   │       │   ├── command/
│   │   │   │       │   │   ├── CreateUserCommand.kt
│   │   │   │       │   │   ├── UpdateUserCommand.kt
│   │   │   │       │   │   └── LoginCommand.kt
│   │   │   │       │   └── query/
│   │   │   │       │       └── UserQuery.kt
│   │   │   │       ├── port/
│   │   │   │       │   ├── incoming/
│   │   │   │       │   │   ├── UserManagementPort.kt
│   │   │   │       │   │   └── AuthenticationPort.kt
│   │   │   │       │   └── outgoing/
│   │   │   │       │       ├── LoadUserPort.kt
│   │   │   │       │       ├── SaveUserPort.kt
│   │   │   │       │       └── PasswordEncoderPort.kt
│   │   │   │       └── event/
│   │   │   │           ├── UserCreatedEvent.kt
│   │   │   │           ├── UserLoggedInEvent.kt
│   │   │   │           └── UserUpdatedEvent.kt
│   │   │   └── build.gradle.kts
│   │   ├── adapter-in/
│   │   │   ├── src/main/kotlin/
│   │   │   │   └── cloud/luigi99/blog/user/
│   │   │   │       ├── web/
│   │   │   │       │   ├── UserController.kt
│   │   │   │       │   ├── AuthController.kt
│   │   │   │       │   ├── request/
│   │   │   │       │   │   ├── CreateUserRequest.kt
│   │   │   │       │   │   ├── UpdateUserRequest.kt
│   │   │   │       │   │   └── LoginRequest.kt
│   │   │   │       │   └── response/
│   │   │   │       │       ├── UserResponse.kt
│   │   │   │       │       └── AuthResponse.kt
│   │   │   │       └── event/
│   │   │   │           └── UserEventHandler.kt
│   │   │   └── build.gradle.kts
│   │   └── adapter-out/
│   │       ├── src/main/kotlin/
│   │       │   └── cloud/luigi99/blog/user/
│   │       │       ├── persistence/
│   │       │       │   ├── entity/
│   │       │       │   │   ├── UserJpaEntity.kt
│   │       │       │   │   └── ProfileJpaEntity.kt
│   │       │       │   ├── repository/
│   │       │       │   │   └── UserJpaRepository.kt
│   │       │       │   └── adapter/
│   │       │       │       └── UserPersistenceAdapter.kt
│   │       │       ├── messaging/
│   │       │       │   └── UserEventPublisher.kt
│   │       │       └── security/
│   │       │           └── BCryptPasswordEncoderAdapter.kt
│   │       └── build.gradle.kts
│   ├── content/
│   │   ├── core/
│   │   │   ├── src/main/kotlin/
│   │   │   │   └── cloud/luigi99/blog/content/
│   │   │   │       ├── domain/
│   │   │   │       │   ├── model/
│   │   │   │       │   │   ├── Post.kt
│   │   │   │       │   │   ├── Category.kt
│   │   │   │       │   │   ├── Tag.kt
│   │   │   │       │   │   ├── Media.kt
│   │   │   │       │   │   └── PostContent.kt
│   │   │   │       │   ├── service/
│   │   │   │       │   │   ├── PostService.kt
│   │   │   │       │   │   ├── CategoryService.kt
│   │   │   │       │   │   ├── TagService.kt
│   │   │   │       │   │   └── MediaService.kt
│   │   │   │       │   └── repository/
│   │   │   │       │       ├── PostRepository.kt
│   │   │   │       │       ├── CategoryRepository.kt
│   │   │   │       │       └── TagRepository.kt
│   │   │   │       ├── application/
│   │   │   │       │   ├── usecase/
│   │   │   │       │   │   ├── ContentManagementUseCase.kt
│   │   │   │       │   │   ├── CategoryManagementUseCase.kt
│   │   │   │       │   │   └── MediaUploadUseCase.kt
│   │   │   │       │   ├── command/
│   │   │   │       │   │   ├── CreatePostCommand.kt
│   │   │   │       │   │   ├── PublishPostCommand.kt
│   │   │   │       │   │   └── CreateCategoryCommand.kt
│   │   │   │       │   └── query/
│   │   │   │       │       ├── PostQuery.kt
│   │   │   │       │       └── CategoryQuery.kt
│   │   │   │       ├── port/
│   │   │   │       │   ├── incoming/
│   │   │   │       │   │   ├── ContentManagementPort.kt
│   │   │   │       │   │   ├── CategoryManagementPort.kt
│   │   │   │       │   │   └── MediaUploadPort.kt
│   │   │   │       │   └── outgoing/
│   │   │   │       │       ├── LoadContentPort.kt
│   │   │   │       │       ├── SaveContentPort.kt
│   │   │   │       │       └── MediaStoragePort.kt
│   │   │   │       └── event/
│   │   │   │           ├── PostPublishedEvent.kt
│   │   │   │           ├── PostCreatedEvent.kt
│   │   │   │           ├── CategoryCreatedEvent.kt
│   │   │   │           └── PostUpdatedEvent.kt
│   │   │   └── build.gradle.kts
│   │   ├── adapter-in/
│   │   │   ├── src/main/kotlin/
│   │   │   │   └── cloud/luigi99/blog/content/
│   │   │   │       ├── web/
│   │   │   │       │   ├── PostController.kt
│   │   │   │       │   ├── CategoryController.kt
│   │   │   │       │   ├── TagController.kt
│   │   │   │       │   ├── MediaController.kt
│   │   │   │       │   ├── request/
│   │   │   │       │   │   ├── CreatePostRequest.kt
│   │   │   │       │   │   ├── PublishPostRequest.kt
│   │   │   │       │   │   └── CreateCategoryRequest.kt
│   │   │   │       │   └── response/
│   │   │   │       │       ├── PostResponse.kt
│   │   │   │       │       ├── CategoryResponse.kt
│   │   │   │       │       └── MediaResponse.kt
│   │   │   │       └── event/
│   │   │   │           └── ContentEventHandler.kt
│   │   │   └── build.gradle.kts
│   │   └── adapter-out/
│   │       ├── src/main/kotlin/
│   │       │   └── cloud/luigi99/blog/content/
│   │       │       ├── persistence/
│   │       │       │   ├── entity/
│   │       │       │   │   ├── PostJpaEntity.kt
│   │       │       │   │   ├── CategoryJpaEntity.kt
│   │       │       │   │   ├── TagJpaEntity.kt
│   │       │       │   │   └── MediaJpaEntity.kt
│   │       │       │   ├── repository/
│   │       │       │   │   ├── PostJpaRepository.kt
│   │       │       │   │   ├── CategoryJpaRepository.kt
│   │       │       │   │   └── TagJpaRepository.kt
│   │       │       │   └── adapter/
│   │       │       │       └── ContentPersistenceAdapter.kt
│   │       │       ├── search/
│   │       │       │   └── ElasticsearchContentAdapter.kt
│   │       │       ├── storage/
│   │       │       │   └── FileStorageAdapter.kt
│   │       │       └── messaging/
│   │       │           └── ContentEventPublisher.kt
│   │       └── build.gradle.kts
│   ├── search/
│   │   ├── core/
│   │   │   ├── src/main/kotlin/
│   │   │   │   └── cloud/luigi99/blog/search/
│   │   │   │       ├── domain/
│   │   │   │       │   ├── model/
│   │   │   │       │   │   ├── SearchResult.kt
│   │   │   │       │   │   ├── SearchCriteria.kt
│   │   │   │       │   │   └── SearchIndex.kt
│   │   │   │       │   ├── service/
│   │   │   │       │   │   └── SearchService.kt
│   │   │   │       │   └── repository/
│   │   │   │       │       └── SearchRepository.kt
│   │   │   │       ├── application/
│   │   │   │       │   ├── usecase/
│   │   │   │       │   │   ├── SearchUseCase.kt
│   │   │   │       │   │   └── IndexingUseCase.kt
│   │   │   │       │   ├── command/
│   │   │   │       │   │   └── IndexContentCommand.kt
│   │   │   │       │   └── query/
│   │   │   │       │       └── SearchQuery.kt
│   │   │   │       └── port/
│   │   │   │           ├── incoming/
│   │   │   │           │   ├── SearchPort.kt
│   │   │   │           │   └── IndexingPort.kt
│   │   │   │           └── outgoing/
│   │   │   │               ├── SearchRepositoryPort.kt
│   │   │   │               └── IndexingEnginePort.kt
│   │   │   └── build.gradle.kts
│   │   ├── adapter-in/
│   │   │   ├── src/main/kotlin/
│   │   │   │   └── cloud/luigi99/blog/search/
│   │   │   │       ├── web/
│   │   │   │       │   ├── SearchController.kt
│   │   │   │       │   ├── request/
│   │   │   │       │   │   └── SearchRequest.kt
│   │   │   │       │   └── response/
│   │   │   │       │       └── SearchResponse.kt
│   │   │   │       └── event/
│   │   │   │           └── SearchEventHandler.kt
│   │   │   └── build.gradle.kts
│   │   └── adapter-out/
│   │       ├── src/main/kotlin/
│   │       │   └── cloud/luigi99/blog/search/
│   │       │       └── elasticsearch/
│   │       │           ├── ElasticsearchSearchAdapter.kt
│   │       │           ├── ElasticsearchIndexAdapter.kt
│   │       │           └── document/
│   │       │               └── SearchDocument.kt
│   │       └── build.gradle.kts
│   ├── analytics/
│   │   ├── core/
│   │   │   ├── src/main/kotlin/
│   │   │   │   └── cloud/luigi99/blog/analytics/
│   │   │   │       ├── domain/
│   │   │   │       │   ├── model/
│   │   │   │       │   │   ├── PageView.kt
│   │   │   │       │   │   ├── UserActivity.kt
│   │   │   │       │   │   └── BlogStatistics.kt
│   │   │   │       │   ├── service/
│   │   │   │       │   │   └── AnalyticsService.kt
│   │   │   │       │   └── repository/
│   │   │   │       │       └── AnalyticsRepository.kt
│   │   │   │       ├── application/
│   │   │   │       │   ├── usecase/
│   │   │   │       │   │   └── AnalyticsUseCase.kt
│   │   │   │       │   ├── command/
│   │   │   │       │   │   └── RecordPageViewCommand.kt
│   │   │   │       │   └── query/
│   │   │   │       │       └── StatisticsQuery.kt
│   │   │   │       └── port/
│   │   │   │           ├── incoming/
│   │   │   │           │   └── AnalyticsPort.kt
│   │   │   │           └── outgoing/
│   │   │   │               └── AnalyticsRepositoryPort.kt
│   │   │   └── build.gradle.kts
│   │   ├── adapter-in/
│   │   │   ├── src/main/kotlin/
│   │   │   │   └── cloud/luigi99/blog/analytics/
│   │   │   │       ├── web/
│   │   │   │       │   └── AnalyticsController.kt
│   │   │   │       └── event/
│   │   │   │           └── AnalyticsEventHandler.kt
│   │   │   └── build.gradle.kts
│   │   └── adapter-out/
│   │       ├── src/main/kotlin/
│   │       │   └── cloud/luigi99/blog/analytics/
│   │       │       └── persistence/
│   │       │           ├── entity/
│   │       │           │   └── PageViewJpaEntity.kt
│   │       │           ├── repository/
│   │       │           │   └── AnalyticsJpaRepository.kt
│   │       │           └── adapter/
│   │       │               └── AnalyticsPersistenceAdapter.kt
│   │       └── build.gradle.kts
│   └── ai/                            # Phase 2에서 구현
│       ├── core/
│       │   └── build.gradle.kts
│       ├── adapter-in/
│       │   └── build.gradle.kts
│       └── adapter-out/
│           └── build.gradle.kts
├── config/
│   ├── detekt/
│   │   └── detekt.yml
│   ├── sonar/
│   │   └── sonar-project.properties
│   └── checkstyle/
│       └── checkstyle.xml
├── docker/
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── docker-compose.dev.yml
├── .github/
│   └── workflows/
├── build.gradle.kts                    # 루트 빌드 스크립트
├── settings.gradle.kts                 # 멀티 모듈 설정
└── gradle.properties
```

### 블로그 구현 대화 요약

#### 패키지 구조 설계 과정
1. **초기 상황**: 기본 Spring Boot + Kotlin 설정만 있는 상태
2. **개발 순서 결정**: 패키지 구조 → DB 스키마 → 헥사고날 아키텍처 코어 구현 → 어댑터는 나중
3. **참고 자료**: `docs/analysis/저장소-분석-보고서.md`의 모듈형 모놀리스 구조
4. **최종 구조**: 도메인명을 최상위로 하고 그 안에 core, adapter-in, adapter-out 구성

#### 핵심 설계 원칙
- **헥사고날 아키텍처**: Core ← Port → Adapter 패턴
- **모듈형 모놀리스**: 도메인별 독립 모듈, 향후 마이크로서비스 분리 준비
- **의존성 방향**: adapter-in → core ← adapter-out
- **모듈별 독립 빌드**: 각 도메인의 core, adapter-in, adapter-out마다 build.gradle.kts
- **K8s 제외**: 인프라는 별도 리포지토리로 관리

#### 구현 우선순위 (Phase 1)
1. 사용자 인증 시스템 (USER-001, USER-004)
2. 기본 콘텐츠 CRUD (CONTENT-001, CONTENT-003, CONTENT-004)  
3. 관리자 대시보드 (ADMIN-001)
4. 기본 검색 기능 (SEARCH-001, SEARCH-005)

#### 다음 단계
1. 서비스별 모듈 디렉토리 구조 생성 (진행 중)
2. 코어 도메인 엔티티 및 비즈니스 로직 구현
3. 데이터베이스 스키마 설계 및 마이그레이션

### Technology Integration Points
- **PostgreSQL**: ACID transactions and complex relational queries
- **Redis**: Session management and application-level caching
- **Elasticsearch**: Full-text search with <100ms response requirement
- **Qdrant**: Vector database for AI embeddings and similarity search
- **Apache Kafka**: Asynchronous event streaming between services
- **OpenAI API**: LLM integration with custom RAG implementation

## Development Evolution Plan

### Phase 1: Foundation (Current)
Focus on modular monolith with clear service boundaries, JWT authentication, basic blog CRUD operations, and simple search functionality.

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