// k6/scenario3_fixed.js
import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    vus: 10,
    duration: '60s'
};

export default function () {
    http.get('http://localhost:8080/api/fix/reference');
    sleep(0.5);
}