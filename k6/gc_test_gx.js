import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 50 },   // 워밍업
        { duration: '3m',  target: 200 },  // 목표 부하
        { duration: '30s', target: 0 },    // 쿨다운
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'],
        http_req_failed:   ['rate<0.01'],
    },
};

export default function () {
    const res = http.get('http://localhost:80/api/gc-stress');
    check(res, { 'status 200': (r) => r.status === 200 });
    sleep(0.5);
}