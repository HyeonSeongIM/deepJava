// k6/scenario1_fixed.js
import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    vus: 10,
    duration: '60s'
};

export default function () {
    http.get('http://localhost:8080/api/fix/static');
    sleep(0.5);
}