# Luigi Log Server - Spring Boot 애플리케이션 Docker 이미지
# 멀티스테이지 빌드로 최적화된 경량 이미지 생성

# Stage 1: 빌드 스테이지 (JDK 필요)
FROM gradle:8.14.3-jdk17-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper 및 빌드 파일들 복사
COPY gradle gradle
COPY gradlew .
COPY gradle.properties .
COPY settings.gradle.kts .
COPY build.gradle.kts .

# 모든 모듈의 빌드 파일들 복사
COPY plugins/ plugins/
COPY libs/ libs/
COPY mains/ mains/
COPY service/ service/

# 실행 권한 부여
RUN chmod +x gradlew

# 의존성 다운로드 (캐시 최적화)
RUN ./gradlew dependencies --no-daemon --quiet

# 애플리케이션 빌드
RUN ./gradlew :mains:monolith-main:bootJar --no-daemon --parallel

# Stage 2: 런타임 스테이지
FROM eclipse-temurin:17-jre-jammy

# 메타데이터 라벨 추가
LABEL maintainer="luigi99" \
      description="Luigi Log Server - 개인 기술 블로그 서버" \
      org.opencontainers.image.title="Luigi Log Server" \
      org.opencontainers.image.description="Spring Boot 기반 개인 기술 블로그 서버" \
      org.opencontainers.image.vendor="Luigi99"

# 애플리케이션 사용자 생성 (보안 강화)
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 작업 디렉토리 설정
WORKDIR /app

# 필요한 패키지 설치 및 정리
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    tzdata && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# JAR 파일 복사 (빌드 스테이지에서)
COPY --from=builder /app/mains/monolith-main/build/libs/*-boot.jar app.jar

# 애플리케이션 파일 소유권 변경
RUN chown -R appuser:appuser /app

# 비특권 사용자로 실행
USER appuser

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]