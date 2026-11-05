    import http from 'k6/http';
    import { check, sleep } from 'k6';

    export const options = {
      vus: 2,
      duration: '20s',
      thresholds: {
        http_req_failed: ['rate<0.01'],
	'http_req_duration{type:write}': ['p(95)<300'],
		},
    };

    const BASE_URL = 'http://api.terrasense.local/api/v1';
    const API_KEY = 'terrasense-secret-key-2026';
    
    export default function () {
      // 1. Escritura (POST simulando reporte de sensor ESP32)
      const payload = JSON.stringify({
        descrip: `Reporte Sensor ESP32 VU-${__VU}`,
        precio: 1500.0,
        vigente: true,
        conStock: true,
        cantStock: 100,
        idCategoria: 'cat00000000000000001',
        idSubCategoria: 'sub00000000000000001',
        linkImagen: 'https://placehold.co/300x300',
        destacado: false,
      });

      const headers = {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${API_KEY}`,
      };

      const postRes = http.post(`${BASE_URL}/producto/new`, payload, {
        headers,
        tags: { type: 'write' },
      });
    
      check(postRes, {
        'POST status es 200': (r) => r.status === 200,
      });

      // 2. Lectura de catalogo (GET)
      const getRes = http.get(`${BASE_URL}/producto/getProductosPorFiltro`, {
        tags: { type: 'read' },
      });

      check(getRes, {
        'GET status es 200': (r) => r.status === 200,
      });

      sleep(1);
    }

