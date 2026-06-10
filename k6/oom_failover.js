import http from 'k6/http';
import { check, sleep } from 'k6';

// nginx(port 80) 경유로 지속 요청 → 어느 서버가 응답했는지 확인
export const options = {
    vus: 10,
    duration: '3m',
    thresholds: {
        http_req_failed: ['rate<0.5'],   // 한 서버 다운 시 일부 실패 허용
    },
};

export default function () {
    const res = http.get('http://localhost/api/gc-stress');

    check(res, {
        'status 200': (r) => r.status === 200,
        'served by': (r) => r.headers['X-Served-By'] !== undefined,
    });

    // 응답한 서버 로깅 (k6 summary에서 확인 가능)
    if (res.headers['X-Served-By']) {
        console.log(`served by: ${res.headers['X-Served-By']}`);
    }

    sleep(0.3);
}
