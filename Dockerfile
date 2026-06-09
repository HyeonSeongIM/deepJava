# 1단계: 빌드
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test

# 2단계: 실행
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
RUN mkdir -p /app/heapdump /app/logs
ENV JAVA_OPTS="-XX:+UseG1GC"
CMD ["sh", "-c", "java \
  -Xms512m -Xmx512m \
  $JAVA_OPTS \
  -Xlog:gc*:stdout:time,uptime,level,tags \
  -Xlog:safepoint:stdout:time,uptime \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/app/heapdump/heapdump.hprof \
  -XX:+ExitOnOutOfMemoryError \
  -jar app.jar"]