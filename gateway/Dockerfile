# 1단계: 빌드
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew :gateway:bootJar -x test --no-daemon

# 2단계: 실행
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/gateway/build/libs/*.jar app.jar
ENV JAVA_OPTS="-XX:+UseG1GC"
EXPOSE 8000
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
