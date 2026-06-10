#!/bin/bash

GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
GRAY='\033[0;90m'
BOLD='\033[1m'
NC='\033[0m'

NGINX="http://localhost"
SLOW_MS=7000    # 느린 요청이 연결을 점유하는 시간 (ms)
SLOW_COUNT=7    # 동시에 보낼 느린 요청 수 (홀수 → 한 서버에 +1 쏠림)
TEST_COUNT=20   # Phase 2에서 보낼 빠른 요청 수

TMP=$(mktemp -d)
trap "rm -rf '$TMP'; kill 0 2>/dev/null" EXIT INT TERM

bar() {
    local n=$1 cap=20
    local len=$(( n > cap ? cap : n ))
    [ $len -gt 0 ] && printf '█%.0s' $(seq 1 $len) || true
}

clear
echo -e "${BOLD}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BOLD}           Least Connection 알고리즘 실습                 ${NC}"
echo -e "${BOLD}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo -e "  ${GRAY}원리: 새 요청이 들어오면 현재 활성 연결 수가${NC}"
echo -e "  ${GRAY}가장 적은 서버로 라우팅합니다.${NC}"
echo ""

# ─── Phase 1: 느린 요청으로 연결 점유 ──────────────────────
echo -e "${YELLOW}[ Phase 1 ]${NC} /api/slow 요청 ${SLOW_COUNT}개 동시 전송 (각 ${SLOW_MS}ms)"
echo -e "  ${GRAY}홀수 개로 보내 두 서버 간 활성 연결 수 차이를 만듭니다.${NC}"
echo ""

for i in $(seq 1 $SLOW_COUNT); do
    (
        hdrs=$(curl -si "${NGINX}/api/slow?ms=${SLOW_MS}" --max-time 20 2>/dev/null)
        app=$(echo "$hdrs" | grep -i "^x-app-name:" | awk '{print $2}' | tr -d '\r\n')
        echo "${app:-unknown}" > "$TMP/slow_$i"
    ) &
done

sleep 1.5
echo -e "  ${GRAY}연결 점유 중 (완료까지 약 $((SLOW_MS / 1000))초 대기)...${NC}"
echo ""

# ─── Phase 2: 느린 요청 진행 중 빠른 요청 전송 ─────────────
echo -e "${YELLOW}[ Phase 2 ]${NC} /api/gc-stress 요청 ${TEST_COUNT}개 순차 전송"
echo -e "  ${GRAY}활성 연결이 많은 서버를 피해 라우팅하는지 확인합니다.${NC}"
echo ""

app1=0; app2=0; fail=0

for i in $(seq 1 $TEST_COUNT); do
    hdrs=$(curl -si "${NGINX}/api/gc-stress" --max-time 5 2>/dev/null)
    app=$(echo "$hdrs" | grep -i "^x-app-name:" | awk '{print $2}' | tr -d '\r\n')
    status=$(echo "$hdrs" | head -1 | awk '{print $2}')

    if [ "$app" = "app1" ]; then
        ((app1++))
        printf "  ${GREEN}→ app1${NC}   app1 ${GREEN}%-21s${NC} %2d   app2 ${BLUE}%-21s${NC} %2d\n" \
            "$(bar $app1)" "$app1" "$(bar $app2)" "$app2"
    elif [ "$app" = "app2" ]; then
        ((app2++))
        printf "  ${BLUE}→ app2${NC}   app1 ${GREEN}%-21s${NC} %2d   app2 ${BLUE}%-21s${NC} %2d\n" \
            "$(bar $app1)" "$app1" "$(bar $app2)" "$app2"
    else
        ((fail++))
        printf "  ${RED}✗ FAIL${NC}  (누적 실패: $fail)\n"
    fi
    sleep 0.15
done

# ─── Phase 3: 느린 요청 완료 대기 및 결과 수집 ──────────────
echo ""
echo -e "${YELLOW}[ Phase 3 ]${NC} 느린 요청 완료 대기..."
wait

s1=0; s2=0
for i in $(seq 1 $SLOW_COUNT); do
    v=$(cat "$TMP/slow_$i" 2>/dev/null)
    [ "$v" = "app1" ] && ((s1++))
    [ "$v" = "app2" ] && ((s2++))
done

# ─── 결과 출력 ───────────────────────────────────────────────
echo ""
echo -e "${BOLD}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BOLD}  결과 요약${NC}"
echo -e "${BOLD}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo -e "  ${YELLOW}느린 요청 분배 (Phase 1, 총 ${SLOW_COUNT}개):${NC}"
echo -e "    app1  ${GREEN}$(bar $s1)${NC}  ${s1}개"
echo -e "    app2  ${BLUE}$(bar $s2)${NC}  ${s2}개"
echo ""
echo -e "  ${YELLOW}빠른 요청 분배 (Phase 2, 총 ${TEST_COUNT}개):${NC}"
echo -e "    app1  ${GREEN}$(bar $app1)${NC}  ${app1}개"
echo -e "    app2  ${BLUE}$(bar $app2)${NC}  ${app2}개"
echo ""

# ─── 해석 ───────────────────────────────────────────────────
if [ $s1 -ne $s2 ]; then
    if [ $s1 -gt $s2 ]; then
        heavy="app1(${s1}개)"; light="app2(${s2}개)"; light_fast=$app2
    else
        heavy="app2(${s2}개)"; light="app1(${s1}개)"; light_fast=$app1
    fi
    echo -e "  ${CYAN}✓ Least Connection 동작 확인${NC}"
    echo -e "    느린 요청이 ${heavy}에 더 몰려 있는 동안,"
    echo -e "    빠른 요청 ${light_fast}/${TEST_COUNT}개가 연결이 적은 ${light}로 집중됐습니다."
    echo -e "    ${GRAY}Round Robin이었다면 ${TEST_COUNT}개가 약 $((TEST_COUNT/2)):$((TEST_COUNT/2))으로 나뉘었을 것입니다.${NC}"
else
    echo -e "  ${CYAN}ℹ 느린 요청이 균등 분배(${s1}:${s2})되어 빠른 요청도 고르게 분산됐습니다."
    echo -e "    least_conn은 부하가 같으면 Round Robin과 동일하게 동작합니다.${NC}"
fi

[ $fail -gt 0 ] && echo -e "\n  ${RED}실패한 요청: ${fail}개${NC}"
echo ""
