    import http from 'k6/http';
    import { check, sleep } from 'k6';
    
    export const options = {
      stages: [
        { duration: '30s', target: 500 },   // Rampa inicial a 500 sensores
        { duration: '45s', target: 2000 },  // Subida a la flota completa (2.000 sensores)
        { duration: '1m', target: 2000 },   // Carga nominal sostenida
        { duration: '15s', target: 0 },     // Enfriamiento
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
        descrip: `Telemetria ESP32 Nodo-${__VU}`,
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
      };
    
      const res = http.post(`${BASE_URL}/producto/new`, payload, params);
    
      check(res, {
        'POST status 200': (r) => r.status === 200,
      });
    
      // Cada sensor reporta cada ~20-30 segundos
      sleep(20 + Math.random() * 10);
    }