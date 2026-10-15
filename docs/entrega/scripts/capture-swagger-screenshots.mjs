/**
 * Captura PNG de cada operación en Swagger UI (requiere API en :3000).
 * Uso: node capture-swagger-screenshots.mjs
 */
import { mkdir } from 'node:fs/promises';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';
import { readFileSync, readdirSync } from 'node:fs';
import puppeteer from 'puppeteer';

const __dir = dirname(fileURLToPath(import.meta.url));
const ROOT = join(__dir, '..');
const OUT = join(ROOT, 'swagger-screenshots');
const MANIFEST = JSON.parse(readFileSync(join(ROOT, 'swagger-guide-manifest.json'), 'utf8'));
const BASE = process.env.API_BASE ?? 'http://127.0.0.1:3000';

const ts = Date.now();

async function api(method, path, { token, body, query } = {}) {
  const url = new URL(`${BASE}${path}`);
  if (query) {
    for (const [k, v] of Object.entries(query)) url.searchParams.set(k, String(v));
  }
  const res = await fetch(url, {
    method,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });
  let json = null;
  try {
    json = await res.json();
  } catch {
    json = null;
  }
  return { status: res.status, body: json };
}

async function prepareSession() {
  const login = await api('POST', '/api/v1/auth/login', {
    body: { email: 'demo@agrocontrol.local', password: 'demo1234' },
  });
  if (login.status !== 201) {
    throw new Error(`Login falló (${login.status}). ¿npm run start:dev y db:setup?`);
  }
  const token = login.body.accessToken;
  const refreshToken = login.body.refreshToken;

  const animals = await api('GET', '/api/v1/animals', { token });
  const paddocks = await api('GET', '/api/v1/paddocks', { token });
  const demoAnimalId = animals.body?.[0]?.id;
  const demoPotreroId = paddocks.body?.[0]?.id;
  if (!demoAnimalId || !demoPotreroId) {
    throw new Error('Semilla demo sin animales/potreros. Ejecuta npm run db:setup');
  }

  const guideAnimal = await api('POST', '/api/v1/animals', {
    token,
    body: {
      codigo: `GUIA-AN-${ts}`,
      nombre: 'Guía Swagger',
      tipo: 'Vaca',
      raza: 'Holstein',
      sexo: 'Hembra',
      peso: 400,
      fechaNacimiento: '2020-06-01',
      potreroNombre: paddocks.body[0]?.nombre ?? 'Potrero 1',
    },
  });
  const guideAnimalId = guideAnimal.body?.id;

  const guidePotrero = await api('POST', '/api/v1/paddocks', {
    token,
    body: {
      nombre: `Guía-Potrero-${ts}`,
      areaHectareas: 1.2,
      estado: 'DISPONIBLE',
      tipoPasto: 'Kikuyo',
      observacion: 'Captura guía',
    },
  });
  const guidePotreroId = guidePotrero.body?.id;

  const prodAnimal = await api('POST', '/api/v1/animals', {
    token,
    body: {
      codigo: `GUIA-PROD-${ts}`,
      tipo: 'Vaca',
      raza: 'Jersey',
      sexo: 'Hembra',
      peso: 380,
      fechaNacimiento: '2021-03-01',
    },
  });
  const prodAnimalId = prodAnimal.body?.id;

  const guideProduction = await api('POST', '/api/v1/production', {
    token,
    body: {
      productType: 'LECHE',
      cantidad: 12,
      unidad: 'L',
      fecha: '2026-01-10',
      jornada: 'MANANA',
      animalId: prodAnimalId,
    },
  });
  const guideProductionId = guideProduction.body?.id;

  const reproAnimal = await api('POST', '/api/v1/animals', {
    token,
    body: {
      codigo: `GUIA-REPRO-${ts}`,
      tipo: 'Vaca',
      raza: 'Holstein',
      sexo: 'Hembra',
      peso: 450,
      fechaNacimiento: '2019-01-01',
    },
  });
  const reproAnimalId = reproAnimal.body?.id;

  const guideReproduction = await api('POST', '/api/v1/reproduction', {
    token,
    body: {
      animalId: reproAnimalId,
      tipoEvento: 'INSEMINACION',
      programada: '2026-02-01',
    },
  });
  const guideReproductionId = guideReproduction.body?.id;

  const taskComplete = await api('POST', '/api/v1/tasks', {
    token,
    body: {
      tipo: 'POTRERO',
      vencimiento: '2026-12-15',
      referencia: 'Potrero 1',
      workType: 'FUMIGAR',
      observacion: 'Guía complete',
    },
  });
  const guideTaskCompleteId = taskComplete.body?.id;

  const taskDelete = await api('POST', '/api/v1/tasks', {
    token,
    body: {
      tipo: 'POTRERO',
      vencimiento: '2026-12-20',
      referencia: 'Potrero 1',
      workType: 'PODA',
      observacion: 'Guía delete',
    },
  });
  const guideTaskDeleteId = taskDelete.body?.id;

  const bodies = {
    register: {
      email: `guia-swagger-${ts}@test.local`,
      password: 'password123',
      nombre: 'Usuario Guía',
      fincaNombre: 'Finca Guía Swagger',
    },
    loginDemo: { email: 'demo@agrocontrol.local', password: 'demo1234' },
    refresh: { refreshToken },
    animalCreate: {
      codigo: `GUIA-POST-${ts}`,
      nombre: 'Alta Swagger',
      tipo: 'Vaca',
      raza: 'Holstein',
      sexo: 'Hembra',
      peso: 410,
      fechaNacimiento: '2020-01-01',
      potreroNombre: 'Potrero 1',
    },
    animalPatch: { nombre: 'Guía Actualizada' },
    potreroCreate: {
      nombre: `Swagger-Potrero-${ts}`,
      areaHectareas: 2,
      estado: 'DISPONIBLE',
      tipoPasto: 'Brachiaria',
      observacion: 'Alta desde Swagger',
    },
    potreroPatch: { observacion: 'Patch guía', areaHectareas: 2.5 },
    potreroSpecies: { especiesPermitidas: ['Vaca', 'Ternero'] },
    productionCreate: {
      productType: 'LECHE',
      cantidad: 8,
      unidad: 'L',
      fecha: '2026-01-12',
      jornada: 'TARDE',
      animalId: prodAnimalId,
    },
    productionPatch: {
      productType: 'LECHE',
      cantidad: 9,
      unidad: 'L',
      fecha: '2026-01-12',
      jornada: 'TARDE',
      animalId: prodAnimalId,
    },
    reproductionCreate: {
      animalId: reproAnimalId,
      tipoEvento: 'DIAGNOSTICO',
      programada: '2026-03-01',
    },
    reproductionPatch: {
      animalId: reproAnimalId,
      tipoEvento: 'PARTO',
      programada: '2026-04-01',
    },
    taskCreate: {
      tipo: 'POTRERO',
      vencimiento: '2026-11-01',
      referencia: 'Potrero 1',
      workType: 'SIEMBRA',
      observacion: 'Swagger task',
    },
    syncPush: {
      changes: [
        {
          entity: 'ANIMAL',
          operation: 'CREATE',
          localId: `local-${ts}`,
          serverId: null,
          payload: {
            codigo: `GUIA-SYNC-${ts}`,
            tipo: 'Ternero',
            raza: 'Cebú',
            sexo: 'Macho',
            peso: 120,
            fechaNacimiento: '2025-01-01',
          },
          clientUpdatedAt: new Date().toISOString(),
        },
      ],
    },
  };

  return {
    token,
    ids: {
      demoAnimalId,
      demoPotreroId,
      guideAnimalId,
      guidePotreroId,
      guideProductionId,
      guideReproductionId,
      guideTaskCompleteId,
      guideTaskDeleteId,
      prodAnimalId,
      reproAnimalId,
    },
    bodies,
  };
}

function resolvePathParams(pathParams, ids) {
  if (!pathParams) return {};
  const out = {};
  for (const [k, v] of Object.entries(pathParams)) {
    out[k] = ids[v] ?? v;
  }
  return out;
}

async function authorizeSwagger(page, token) {
  await page.goto(`${BASE}/api/docs`, { waitUntil: 'networkidle2', timeout: 60000 });
  await page.waitForSelector('.swagger-ui', { timeout: 30000 });
  const authorizeBtn = await page.$('button.authorize');
  if (authorizeBtn) {
    await authorizeBtn.click();
    await page.waitForSelector('.auth-container', { timeout: 5000 }).catch(() => null);
    const inputs = await page.$$('.auth-container input');
    for (const input of inputs) {
      await input.click({ clickCount: 3 });
      await input.type(token, { delay: 5 });
    }
    const authSubmit = await page.$('.auth-btn-wrapper button.btn.modal-btn.auth.authorize');
    if (authSubmit) await authSubmit.click();
    const close = await page.$('.auth-btn-wrapper button.btn.modal-btn.auth.btn-done');
    if (close) await close.click();
    await new Promise((r) => setTimeout(r, 500));
  }
}

async function screenshotAuthorize(page, outPath) {
  await page.goto(`${BASE}/api/docs`, { waitUntil: 'networkidle2' });
  await page.waitForSelector('.swagger-ui');
  const btn = await page.$('button.authorize');
  if (btn) await btn.click();
  await new Promise((r) => setTimeout(r, 800));
  await page.screenshot({ path: outPath, fullPage: false });
  const close = await page.$('.auth-btn-wrapper button.btn.modal-btn.auth.btn-done');
  if (close) await close.click();
}

async function captureOperation(page, spec, ctx) {
  const method = spec.method.toUpperCase();
  const pathParams = resolvePathParams(spec.pathParams, ctx.ids);
  const query = spec.query ?? null;
  const body = spec.bodyKey ? ctx.bodies[spec.bodyKey] : null;
  const bodyStr = body ? JSON.stringify(body, null, 2) : null;

  const result = await page.evaluate(
    async ({ method, pathMatch, pathExclude, pathParams, query, bodyStr }) => {
      const sleep = (ms) => new Promise((r) => setTimeout(r, ms));
      document.querySelectorAll('[data-capture-target]').forEach((el) => el.removeAttribute('data-capture-target'));
      const blocks = [...document.querySelectorAll('.opblock')];
      const block = blocks.find((b) => {
        const m = b.querySelector('.opblock-summary-method')?.textContent?.trim().toUpperCase();
        const pathEl = b.querySelector('.opblock-summary-path');
        const p = (pathEl?.textContent ?? '').replace(/\s/g, '');
        if (m !== method) return false;
        if (!p.includes(pathMatch.replace(/\s/g, ''))) return false;
        if (pathExclude && p.includes(pathExclude)) return false;
        return true;
      });
      if (!block) return { ok: false, err: `No opblock ${method} ${pathMatch}` };

      block.scrollIntoView({ block: 'center', behavior: 'instant' });
      await sleep(300);
      const summary = block.querySelector('.opblock-summary');
      if (!block.classList.contains('is-open') && summary) summary.click();
      await sleep(400);

      const tryBtn = block.querySelector('button.try-out__btn');
      if (tryBtn && tryBtn.textContent?.includes('Try it out')) tryBtn.click();
      await sleep(300);

      if (pathParams) {
        for (const [name, value] of Object.entries(pathParams)) {
          const row = [...block.querySelectorAll('tr[data-param-name]')].find(
            (tr) => tr.getAttribute('data-param-name') === name,
          );
          const input = row?.querySelector('input') ?? block.querySelector(`input[data-param-name="${name}"]`);
          if (input) {
            input.value = value;
            input.dispatchEvent(new Event('input', { bubbles: true }));
            input.dispatchEvent(new Event('change', { bubbles: true }));
          }
        }
      }

      if (query) {
        for (const [name, value] of Object.entries(query)) {
          const row = [...block.querySelectorAll('tr[data-param-name]')].find(
            (tr) => tr.getAttribute('data-param-name') === name,
          );
          const input = row?.querySelector('input');
          if (input) {
            input.value = value;
            input.dispatchEvent(new Event('input', { bubbles: true }));
            input.dispatchEvent(new Event('change', { bubbles: true }));
          }
        }
      }

      const textarea =
        block.querySelector('textarea.body-param__text') ??
        block.querySelector('.body-param textarea') ??
        block.querySelector('textarea');
      if (bodyStr && textarea) {
        textarea.value = bodyStr;
        textarea.dispatchEvent(new Event('input', { bubbles: true }));
        textarea.dispatchEvent(new Event('change', { bubbles: true }));
      }

      const execute = block.querySelector('button.execute');
      if (execute) execute.click();
      await sleep(2000);
      block.setAttribute('data-capture-target', '1');
      return { ok: true };
    },
    {
      method,
      pathMatch: spec.pathMatch,
      pathExclude: spec.pathExclude ?? null,
      pathParams,
      query,
      bodyStr,
    },
  );

  if (!result?.ok) {
    console.warn(`  ⚠ ${spec.screenshot}: ${result?.err ?? 'falló evaluate'}`);
    await page.screenshot({ path: join(OUT, spec.screenshot), fullPage: true });
    return;
  }

  await page
    .waitForSelector('.opblock[data-capture-target="1"] .live-responses-table', { timeout: 8000 })
    .catch(() => null);

  await page.evaluate(() => {
    const block = document.querySelector('.opblock[data-capture-target="1"]');
    const resp =
      block?.querySelector('.live-responses-wrapper') ?? block?.querySelector('.responses-wrapper');
    resp?.scrollIntoView({ block: 'nearest', behavior: 'instant' });
  });
  await new Promise((r) => setTimeout(r, 400));

  await screenshotOpblockClip(page, join(OUT, spec.screenshot));
}

function pngDimensions(filePath) {
  const b = readFileSync(filePath);
  return { width: b.readUInt32BE(16), height: b.readUInt32BE(20) };
}

async function screenshotOpblockClip(page, outPath) {
  await page.evaluate(() => {
    const block = document.querySelector('.opblock[data-capture-target="1"]');
    block?.scrollIntoView({ block: 'start', behavior: 'instant' });
  });
  await new Promise((r) => setTimeout(r, 350));

  const clip = await page.evaluate(() => {
    const block = document.querySelector('.opblock[data-capture-target="1"]');
    if (!block) return null;

    const nodes = [block, ...block.querySelectorAll('*')];
    let x1 = Infinity;
    let y1 = Infinity;
    let x2 = -Infinity;
    let y2 = -Infinity;
    for (const el of nodes) {
      const r = el.getBoundingClientRect();
      if (r.width < 1 || r.height < 1) continue;
      x1 = Math.min(x1, r.x);
      y1 = Math.min(y1, r.y);
      x2 = Math.max(x2, r.x + r.width);
      y2 = Math.max(y2, r.y + r.height);
    }
    if (!Number.isFinite(x1)) return null;

    const pad = 12;
    const maxH = 2200;
    const x = Math.max(0, x1 - pad);
    const y = Math.max(0, y1 - pad);
    const width = Math.min(window.innerWidth - x, x2 - x1 + pad * 2);
    const height = Math.min(maxH, y2 - y1 + pad * 2);

    return { x, y, width, height };
  });

  if (!clip || clip.width < 50 || clip.height < 200) {
    console.warn(`  ⚠ clip inválido en ${outPath}, captura viewport visible`);
    const vp = page.viewport();
    await page.screenshot({
      path: outPath,
      clip: { x: 0, y: 0, width: vp.width, height: Math.min(vp.height, 860) },
    });
  } else {
    await page.evaluate((c) => {
      window.scrollTo(0, Math.max(0, c.y - 16));
    }, clip);
    await new Promise((r) => setTimeout(r, 250));
    await page.screenshot({ path: outPath, clip, captureBeyondViewport: true });
  }

  await page.evaluate(() => {
    document.querySelector('.opblock[data-capture-target="1"]')?.removeAttribute('data-capture-target');
  });
}

function validateCaptures() {
  const bad = [];
  for (const name of readdirSync(OUT).filter((f) => f.endsWith('.png') && !f.startsWith('00-'))) {
    const { height } = pngDimensions(join(OUT, name));
    if (height < 200) bad.push(`${name} (${height}px alto)`);
  }
  if (bad.length) {
    throw new Error(`Capturas demasiado bajas (revisar Swagger):\n${bad.join('\n')}`);
  }
}

async function main() {
  await mkdir(OUT, { recursive: true });
  console.log('Preparando sesión API…');
  const ctx = await prepareSession();
  console.log('Iniciando navegador…');
  const browser = await puppeteer.launch({ headless: 'new' });
  const page = await browser.newPage();
  await page.setViewport({ width: 1280, height: 900, deviceScaleFactor: 2 });

  console.log('Captura 00 authorize…');
  await authorizeSwagger(page, ctx.token);
  await screenshotAuthorize(page, join(OUT, '00-swagger-authorize.png'));

  for (const section of MANIFEST.sections) {
    if (section.kind === 'intro') continue;
    console.log(`Captura ${section.screenshot}…`);
    await captureOperation(page, section, ctx);
  }

  validateCaptures();
  await browser.close();
  console.log(`Listo: ${OUT}`);
}

main().catch((e) => {
  console.error(e);
  process.exit(1);
});
