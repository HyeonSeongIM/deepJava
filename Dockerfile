# 1단계: Render에서 직접 빌드
FROM gradle:8-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon -x test

# 2단계: 빌드된 JAR 실행
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
CMD ["java", "-jar", "-Duser.timezone=Asia/Seoul", "/app/app.jar"]