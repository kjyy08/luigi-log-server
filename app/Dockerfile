FROM eclipse-temurin:21-jre-jammy

# 애플리케이션 사용자 생성
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사
COPY app/build/libs/*.jar app.jar

# 비특권 사용자로 실행
USER appuser

# 애플리케이션 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
