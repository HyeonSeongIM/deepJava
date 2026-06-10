#!/bin/bash

# 색상
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
GRAY='\033[0;90m'
BOLD='\033[1m'
NC='\033[0m'

app1_count=0
app2_count=0
fail_count=0

clear
echo -e "${BOLD}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BOLD}   Nginx 로드밸런서 모니터링  (Ctrl+C 종료)${NC}"
echo -e "${BOLD}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GRAY}   OOM 발동: curl http://localhost:8081/api/oom${NC}"
echo -e "${BOLD}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""

while true; do
    timestamp=$(date '+%H:%M:%S')

    headers=$(curl -s -D - "http://localhost/api/gc-stress" -o /dev/null --max-time 2 2>/dev/null)
    status=$(echo "$headers" | head -1 | awk '{print $2}')
    app_name=$(echo "$headers" | grep -i "^x-app-name:" | awk '{print $2}' | tr -d '\r\n')

    if [ "$status" = "200" ] && [ -n "$app_name" ]; then
        if [ "$app_name" = "app1" ]; then
            ((app1_count++))
            bar1=$(printf '█%.0s' $(seq 1 $((app1_count > 30 ? 30 : app1_count))))
            bar2=$(printf '█%.0s' $(seq 1 $((app2_count > 30 ? 30 : app2_count))))
            printf "${GREEN}[%s] ▶ app1${NC}   app1 ${GREEN}%-30s${NC} %3d\n" "$timestamp" "$bar1" "$app1_count"
            printf "         app2 ${BLUE}%-30s${NC} %3d\n" "$bar2" "$app2_count"
        else
            ((app2_count++))
            bar1=$(printf '█%.0s' $(seq 1 $((app1_count > 30 ? 30 : app1_count))))
            bar2=$(printf '█%.0s' $(seq 1 $((app2_count > 30 ? 30 : app2_count))))
            printf "${BLUE}[%s] ▶ app2${NC}   app1 ${GREEN}%-30s${NC} %3d\n" "$timestamp" "$bar1" "$app1_count"
            printf "         app2 ${BLUE}%-30s${NC} %3d\n" "$bar2" "$app2_count"
        fi
    else
        ((fail_count++))
        bar1=$(printf '█%.0s' $(seq 1 $((app1_count > 30 ? 30 : app1_count))))
        bar2=$(printf '█%.0s' $(seq 1 $((app2_count > 30 ? 30 : app2_count))))
        printf "${RED}[%s] ✗ FAIL${NC}  app1 ${GREEN}%-30s${NC} %3d\n" "$timestamp" "$bar1" "$app1_count"
        printf "         app2 ${BLUE}%-30s${NC} %3d  ${RED}(실패: %d)${NC}\n" "$bar2" "$app2_count" "$fail_count"
    fi

    echo ""
    sleep 0.5
done
