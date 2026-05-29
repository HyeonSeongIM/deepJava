import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    stages: [
        { duration: '10s', target: 100 },
        { duration: '20s', target: 500 },
        { duration: '30s', target: 1000 },
        { duration: '10s', target: 0 },
    ]
};

export default function () {
    http.get('http://localhost:8080/api/fix/static');
    sleep(0.5);
}