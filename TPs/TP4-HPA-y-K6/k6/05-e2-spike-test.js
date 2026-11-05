    import http from 'k6/http';
    import { check } from 'k6';

    export const options = {
      stages: [
        { duration: '5s', target: 1500 },  // Pico repentino vertical (Spike)
        { duration: '40s', target: 1500 }, // Sostener antes de que el HPA logre salvarlo
        { duration: '5s', target: 0 },
      ],
    };

    const BASE_URL = 'http://api.terrasense.local/api/v1';
    const API_KEY = 'terrasense-secret-key-2026';

    export default function () {
      const payload = JSON.stringify({
        descrip: `Spike Sensor VU-${__VU}-${__ITER}`,
        precio: 9999.9,
        vigente: true,
        conStock: true,
        cantStock: 100,
        idCategoria: 'cat00000000000000001',
        idSubCategoria: 'sub00000000000000001',
        linkImagen: 'https://placehold.co/300x300',
        destacado: true,
      });

      const headers = {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${API_KEY}`,
      };

      const postRes = http.post(`${BASE_URL}/producto/new`, payload, {
        headers,
        timeout: '4s',
      });

      const getRes = http.get(`${BASE_URL}/producto/list`, {
        timeout: '4s',
      });

      check(postRes, {
        'status 200': (r) => r.status === 200,
        'timeout o 502/504': (r) => r.status === 0 || r.status >= 500,
      });
    }