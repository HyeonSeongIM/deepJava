// k6/scenario1.js
import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    vus: 10,          // 가상 유저 10명
    duration: '60s'  // 60초 동안
};

export default function () {
    http.get('http://localhost:8080/api/leak/static');
    sleep(0.5);
}