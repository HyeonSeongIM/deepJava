#!/bin/bash

# 경로 설정
BASE_DIR=$(dirname "$0")
JAR="$BASE_DIR/build/libs/deepJava-0.0.1-SNAPSHOT.jar"
LOG="$BASE_DIR/oom.log"
RESTART_LOG="$BASE_DIR/restart.log"
HEAP_DUMP="$BASE_DIR/heapdump.hprof"

echo "OOM 발생! $(date)" >> "$LOG"

sleep 10

rm -f "$HEAP_DUMP"

nohup java \
  -Xms256m \
  -Xmx256m \
  -XX:+UseG1GC \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath="$HEAP_DUMP" \
  -XX:+ExitOnOutOfMemoryError \
  -XX:OnOutOfMemoryError="$BASE_DIR/restart.sh" \
  -jar "$JAR" \
  --server.port=8080 \
  >> "$RESTART_LOG" 2>&1 &

echo "재시작 완료 $(date)" >> "$LOG"