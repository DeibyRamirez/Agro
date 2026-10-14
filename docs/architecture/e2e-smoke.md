# Smoke test E2E (manual)

## Backend

1. `docker compose -f infra/docker-compose.yml up -d`
2. `cd backend && cp .env.example .env && npm install`
3. `npx prisma migrate deploy && npx prisma db seed`
4. `npm run start:dev`
5. Abrir http://localhost:3000/api/docs
6. `npm run test && npm run test:e2e`

## Android (emulador)

1. API en host: puerto 3000.
2. App usa `http://10.0.2.2:3000/api/v1/`.
3. Login: `demo@agrocontrol.local` / `demo1234`
4. Verificar dashboard KPIs, lista animales, sync en **Más** (tocar contenido).
5. Reportes semanal/mensual con sesión activa.

## Offline

1. Agregar animal sin red → aparece en lista.
2. Activar red → WorkManager sincroniza (log `sync/push`).
