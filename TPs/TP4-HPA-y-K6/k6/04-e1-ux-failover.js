    import http from 'k6/http';
    import { check, sleep } from 'k6';
    import { browser } from 'k6/browser';

    export const options = {
      scenarios: {
        carga_estable_api: {
          executor: 'constant-vus',
          vus: 10,
          duration: '40s',
          exec: 'apiTraffic',
        },
        navegador_ux: {
          executor: 'shared-iterations',
          vus: 1,
          iterations: 1,
          startTime: '8s',
          maxDuration: '30s',
          exec: 'captureFrontendUX',
          options: {
            browser: {
              type: 'chromium',
            },
          },
        },
      },
    };

    export function apiTraffic() {
      const res = http.get('http://api.terrasense.local/api/v1/producto/getProductosPorFiltro', {
        timeout: '3s',
      });
      check(res, {
        'API responde 200': (r) => r.status === 200,
      });
      sleep(0.5);
    }

    export async function captureFrontendUX() {
      const page = await browser.newPage();
      try {
        await page.goto('http://terrasense.local/');
        // Esperamos 4 segundos para que React intente consultar la API caida y renderice el estado de error
        await page.waitForTimeout(4000);
        await page.screenshot({ path: 'TPs/TP4-HPA-y-K6/evidence/falla-ux.png' });
      } finally {
        await page.close();
      }
    }