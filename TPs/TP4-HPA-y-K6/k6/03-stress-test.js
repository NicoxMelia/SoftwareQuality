    import http from 'k6/http';
    import { check, sleep } from 'k6';
    
    export const options = {
      stages: [
        { duration: '20s', target: 100 },  // Carga moderada
        { duration: '30s', target: 300 },  // Carga alta (HPA al maximo)
        { duration: '30s', target: 600 },  // Sobrecarga severa
        { duration: '30s', target: 1000 }, // Punto de quiebre
        { duration: '15s', target: 0 },    // Recuperacion
      ],
      thresholds: {
        http_req_failed: ['rate<0.01'],
        'http_req_duration{type:write}': ['p(95)<300'],
      },
    };
    
    const BASE_URL = 'http://api.terrasense.local/api/v1';
    const API_KEY = 'terrasense-secret-key-2026';
    
    export default function () {
      const payload = JSON.stringify({
        descrip: `Stress ESP32 Nodo-${__VU}`,
        precio: 1500.0,
        vigente: true,
        conStock: true,
        cantStock: 50,
        idCategoria: 'cat00000000000000001',
        idSubCategoria: 'sub00000000000000001',
        linkImagen: 'https://placehold.co/300x300',
        destacado: false,
      });
    
      const params = {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${API_KEY}`,
        },
        tags: { type: 'write' },
        timeout: '5s',
      };
    
      const res = http.post(`${BASE_URL}/producto/new`, payload, params);
    
      check(res, {
        'status es 200': (r) => r.status === 200,
        'sin error 5xx': (r) => r.status < 500,
      });
    
      sleep(0.2);
    }