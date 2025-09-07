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

### Service Structure
```
service/
├── user-service/
│   ├── core/          # Pure domain logic
│   ├── adapter-in/    # Controllers, event handlers
│   └── adapter-out/   # Repositories, external services
├── content-service/   # Blog posts, categories, tags
├── ai-chat-service/   # RAG system, MCP servers
├── search-service/    # Elasticsearch integration
└── analytics-service/ # Blog statistics, visitor tracking
```

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

### Requirements Documents (`docs/requirements/`)
- **기능적-요구사항.csv**: 25 functional user stories organized by epics
- **비기능적-요구사항.csv**: 30 non-functional requirements with metrics
- **기술적-요구사항.csv**: 35 technical requirements with implementation details
- **깃허브-이슈-템플릿.csv**: GitHub issue templates for project management

### Analysis Documents (`docs/analysis/`)
- **저장소-분석-보고서.md**: Analysis of the target repository that this project is cloning/inspired by

## Requirements Management

### GitHub Issue Creation
Use the CSV files in `docs/requirements/` to systematically create GitHub issues:
- Epic-level features with clear acceptance criteria
- User stories with story points and dependencies
- Technical tasks with implementation specifications
- Bug report and feature request templates

### Priority Classification
- **필수 (Must Have)**: Core functionality and critical quality requirements
- **권장 (Should Have)**: Important but not Phase 1 requirements
- **선택 (Could Have)**: Nice-to-have features that add value

When working with requirements:
1. Review the CSV files to understand the full scope
2. Use the Korean descriptions for better comprehension
3. Follow the dependency relationships when planning implementation
4. Reference the GitHub issue templates for consistent issue creation