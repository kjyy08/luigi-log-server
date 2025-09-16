# Luigi Log Server - Claude Code 서브 에이전트 가이드

Luigi Log Server 프로젝트를 위한 서브 에이전트 사용 가이드입니다.  
각 에이전트는 특정 영역에 특화되어 있으며, 프로젝트의 다양한 개발 단계에서 최적화된 지원을 제공합니다.

## 🎯 서브 에이전트 개요

### 1. `api-developer` - REST API 전문가
**언제 사용하나요?**
- REST API 엔드포인트 설계 및 구현
- OpenAPI 문서화 작업
- HTTP 상태 코드 및 응답 모델링
- API 버전 관리 및 오류 처리

### 2. `backend-engineer` - 백엔드 아키텍처 전문가
**언제 사용하나요?**
- 헥사고날 아키텍처 설계
- 도메인 모델링 및 비즈니스 로직 구현
- Spring Security + JWT 인증 시스템
- Apache Kafka 이벤트 주도 아키텍처

### 3. `database-schema-engineer` - 데이터베이스 설계 전문가
**언제 사용하나요?**
- PostgreSQL/MySQL 스키마 설계
- JPA 엔티티 매핑 및 관계 설정
- Flyway 마이그레이션 전략
- Redis 캐싱 계층 통합

### 4. `frontend-engineer` - 프론트엔드 개발 전문가
**언제 사용하나요?**
- React 컴포넌트 아키텍처
- Tailwind CSS 반응형 디자인
- 상태 관리 (Redux, Zustand)
- 성능 최적화 및 접근성

### 5. `github-manager` - GitHub 워크플로우 전문가
**언제 사용하나요?**
- Issue 생성 및 관리
- Pull Request 워크플로우
- 브랜치 전략 및 커밋 컨벤션
- 프로젝트 보드 관리

### 6. `infrastructure-engineer` - 클라우드 네이티브 인프라 전문가
**언제 사용하나요?**
- K3s 클러스터 관리
- Docker 컨테이너화
- ArgoCD GitOps 워크플로우
- LGTM 스택 모니터링

### 7. `product-manager` - 제품 기획 및 요구사항 전문가
**언제 사용하나요?**
- 요구사항 분석 및 문서화
- 사용자 스토리 작성
- 프로젝트 로드맵 계획
- 이해관계자 관리

### 8. `prompt-engineer` - AI 프롬프트 최적화 전문가
**언제 사용하나요?**
- AI 챗봇 시스템 프롬프트 설계
- RAG 시스템 프롬프트 최적화
- MCP 프로토콜 프롬프트 작성
- LLM 성능 튜닝

### 9. `testing-engineer` - 테스팅 전략 전문가
**언제 사용하나요?**
- JUnit 5 + Kotest 테스트 스위트
- TestContainers 통합 테스트
- Kover 코드 커버리지 분석
- 성능 테스트 및 CI/CD 통합

## 🎯 상황별 에이전트 선택 가이드

### 새로운 기능 개발 시

| 단계        | 추천 에이전트                    | 작업 내용                    |
|-----------|----------------------------|--------------------------|
| 1. 기획     | `product-manager`          | 요구사항 분석, 사용자 스토리 작성      |
| 2. 설계     | `backend-engineer`         | 도메인 모델링, 아키텍처 설계         |
| 3. 데이터    | `database-schema-engineer` | 스키마 설계, 엔티티 매핑           |
| 4. 테스트    | `testing-engineer`         | 테스트 전략, 코드 커버리지          |
| 5. API    | `api-developer`            | REST API 설계, OpenAPI 문서화 |
| 6. 프론트엔드  | `frontend-engineer`        | React 컴포넌트, UI/UX        |
| 7. GitHub | `github-manager`           | Issue/PR 관리, 브랜치 전략      |

## 💡 실용적인 프롬프트 예시

### 1. `backend-engineer` - 백엔드 아키텍처 전문가
```
@backend-engineer User 도메인의 회원가입 UseCase를 구현해주세요. 이메일 중복 검증 비즈니스 룰, BCrypt 비밀번호 암호화, UserRegistered 도메인 이벤트 발행, 그리고 UserPort 인터페이스를 포함한 완전한 헥사고날 아키텍처로 구현해주세요.
```

### 2. `api-developer` - REST API 전문가
```
@api-developer 블로그 포스트 REST API를 구현해주세요. Pageable 기반 페이지네이션, 카테고리/태그 다중 필터링, 검색 키워드 지원, OpenAPI 3.0 문서화, 그리고 ApiResponse 래퍼를 사용한 일관된 응답 형식으로 구현해주세요.
```

### 3. `database-schema-engineer` - 데이터베이스 설계 전문가
```
@database-schema-engineer Post-Category-Tag 다대다 관계를 JPA 엔티티로 설계해주세요. 성능 최적화를 위한 복합 인덱스, N+1 문제 해결을 위한 fetch join 전략, Flyway V2__create_content_schema.sql 마이그레이션 스크립트까지 포함해주세요.
```

### 4. `testing-engineer` - 테스팅 전략 전문가
```
@testing-engineer RegisterUserUseCase의 테스트 스위트를 작성해주세요. Kotest BehaviorSpec 스타일, MockK를 활용한 협력 객체 모킹, 성공/실패 시나리오 전체 커버리지, 그리고 TestContainers 기반 통합 테스트로 80% 이상 커버리지를 달성해주세요.
```

### 5. `frontend-engineer` - 프론트엔드 개발 전문가
```
@frontend-engineer 블로그 포스트 목록 React 컴포넌트를 구현해주세요. TypeScript + Tailwind CSS 기반 반응형 카드 레이아웃, react-intersection-observer를 활용한 무한 스크롤, React Query 캐싱, 그리고 Storybook 스토리까지 포함해주세요.
```

### 6. `github-manager` - GitHub 워크플로우 전문가
```
@github-manager AI 챗봇 MVP 구현을 위한 GitHub 프로젝트를 설정해주세요. Epic/Story/Task 3단계 이슈 구조, 2주 스프린트 마일스톤, enhancement/bug/ai 라벨 체계, 그리고 칸반 보드 자동화 워크플로우까지 포함해주세요.
```

### 7. `infrastructure-engineer` - 클라우드 네이티브 인프라 전문가
```
@infrastructure-engineer Luigi Log Server K3s 배포 환경을 구축해주세요. 멀티스테이지 Dockerfile, ConfigMap/Secret 분리, HPA/PDB 설정, ArgoCD Application 매니페스트, 그리고 Prometheus 메트릭 수집을 위한 ServiceMonitor까지 포함해주세요.
```

### 8. `product-manager` - 제품 기획 및 요구사항 전문가
```
@product-manager AI 챗봇 MVP의 사용자 스토리를 작성해주세요. 블로그 콘텐츠 Q&A 핵심 시나리오, Given-When-Then 수용 기준, 3초 이하 응답시간 성능 지표, 그리고 사용자 만족도 측정 방법을 포함한 완전한 PRD를 만들어주세요.
```

### 9. `prompt-engineer` - AI 프롬프트 최적화 전문가
```
@prompt-engineer 블로그 콘텐츠 분석 AI 챗봇의 시스템 프롬프트를 설계해주세요. Qdrant 벡터 검색 기반 RAG 컨텍스트 활용, 기술 블로그 톤앤매너 유지, 3초 이하 응답 최적화를 위한 토큰 제한, 그리고 hallucination 방지 가드레일까지 포함해주세요.
```
